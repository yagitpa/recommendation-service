package com.cw.starbank.recommendation_service.repository;

import com.cw.starbank.recommendation_service.aggregator.UserTransactionAggregator;
import com.cw.starbank.recommendation_service.dto.ProductInfo;
import com.cw.starbank.recommendation_service.util.SqlQueries;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Репозиторий для доступа к данным транзакций и продуктов.
 * <p>
 * Отвечает за выполнение SQL-запросов к базе данных и конвертацию данных
 * из формата базы данных в объекты приложения. Все SQL-запросы берутся из
 * {@link SqlQueries} для централизованного управления.
 * </p>
 *
 * <h3>Основные функции:</h3>
 * <ul>
 *   <li>Агрегация транзакций пользователя с конвертацией копеек в рубли</li>
 *   <li>Проверка существования пользователей</li>
 *   <li>Получение информации о продуктах</li>
 *   <li>Проверка подключения к БД</li>
 * </ul>
 *
 * <h3>Особенности работы с H2:</h3>
 * <ul>
 *   <li>Использование экранирования кавычками для всех идентификаторов</li>
 *   <li>Конвертация типов данных (Integer → BigDecimal)</li>
 *   <li>Обработка NULL значений через COALESCE в SQL-запросах</li>
 * </ul>
 *
 * @see SqlQueries
 * @see UserTransactionAggregator
 * @since 1.0.0
 */
@Repository
public class RecommendationRepository {

    private static final Logger logger = LoggerFactory.getLogger(RecommendationRepository.class);
    private static final BigDecimal KOPECKS_IN_RUBLE = new BigDecimal("100");

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public RecommendationRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    /**
     * Проверяет подключение к базе данных.
     *
     * @return true если подключение успешно, false в случае ошибки
     */
    public boolean checkDatabaseConnection() {
        try {
            // Простой запрос, который точно работает
            Integer result = jdbcTemplate.queryForObject(
                    "SELECT 1",
                    Integer.class
            );
            logger.debug("Database connection check: result = {}", result);
            return result != null && result == 1;
        } catch (Exception e) {
            logger.error("Database connection check failed: {}", e.getMessage());
            return false;
        }
    }

    /**
     * Проверяет существование пользователя в системе.
     *
     * @param userId идентификатор пользователя
     * @return true если пользователь существует, false в противном случае
     */
    public boolean userExists(UUID userId) {
        try {
            Boolean exists = jdbcTemplate.queryForObject(
                    SqlQueries.USER_EXISTS,
                    Boolean.class,
                    userId
            );
            return exists != null && exists;
        } catch (Exception e) {
            logger.error("Error checking user existence for {}: {}", userId, e.getMessage());
            return false;
        }
    }

    /**
     * Получает агрегатор транзакций для указанного пользователя.
     * <p>
     * Выполняет запрос к базе данных, получает все транзакции пользователя,
     * конвертирует суммы из копеек в рубли и добавляет их в агрегатор.
     * </p>
     *
     * @param userId идентификатор пользователя
     * @return агрегатор с данными транзакций пользователя
     */
    public UserTransactionAggregator getUserTransactionAggregator(UUID userId) {
        UserTransactionAggregator aggregator = new UserTransactionAggregator();

        try {
            jdbcTemplate.query(
                    SqlQueries.GET_USER_TRANSACTIONS_BY_PRODUCT_TYPE,
                    (ResultSet rs) -> {
                        String productType = rs.getString("product_type");
                        String transactionType = rs.getString("transaction_type");

                        // Поле AMOUNT в базе INTEGER (копейки), преобразуем в BigDecimal
                        BigDecimal amountInKopecks = BigDecimal.valueOf(rs.getInt("amount"));

                        // Конвертируем из копеек в рубли
                        BigDecimal amountInRubles = convertKopecksToRubles(amountInKopecks);

                        // Важное замечание: База возвращает 'DEPOSIT' или 'WITHDRAW'
                        // UserTransactionAggregator ожидает те же значения
                        // Так что преобразование не требуется

                        logger.trace("Transaction - Product: {}, Type: {}, Kopecks: {}, Rubles: {}",
                                productType, transactionType, amountInKopecks, amountInRubles);

                        // Добавляем транзакцию в агрегатор
                        aggregator.addTransaction(productType, transactionType, amountInRubles);
                    },
                    userId
            );

            logger.debug("Aggregated transactions for user {}: {} product types found",
                    userId, aggregator.getStatsByProductType().size());

        } catch (Exception e) {
            logger.error("Error aggregating transactions for user {}: {}", userId, e.getMessage(), e);
        }

        return aggregator;
    }

    /**
     * Проверяет, использует ли пользователь продукт определенного типа.
     * <p>
     * Прямой запрос к базе для проверки условия без агрегации всех транзакций.
     * </p>
     *
     * @param userId идентификатор пользователя
     * @param productType тип продукта (DEBIT, INVEST, SAVING, CREDIT)
     * @return true если пользователь использует продукт данного типа
     */
    public boolean usesProductType(UUID userId, String productType) {
        try {
            Boolean uses = jdbcTemplate.queryForObject(
                    SqlQueries.USES_PRODUCT_TYPE,
                    Boolean.class,
                    userId, productType
            );
            return uses != null && uses;
        } catch (Exception e) {
            logger.error("Error checking product type usage for user {}: {}", userId, e.getMessage());
            return false;
        }
    }

    /**
     * Рассчитывает сумму операций по типу продукта и типу операции.
     * <p>
     * Возвращает сумму уже конвертированную в рубли.
     * </p>
     *
     * @param userId идентификатор пользователя
     * @param productType тип продукта
     * @param transactionType тип операции (DEPOSIT/WITHDRAWAL)
     * @return сумма в рублях
     */
    public BigDecimal calculateAmountByType(UUID userId, String productType, String transactionType) {
        try {
            // Запрос возвращает сумму в копейках
            Integer amountInKopecks = jdbcTemplate.queryForObject(
                    SqlQueries.CALCULATE_AMOUNT_BY_TYPE,
                    Integer.class,
                    userId, productType, transactionType
            );

            if (amountInKopecks == null) {
                return BigDecimal.ZERO;
            }

            // Конвертируем в рубли
            return convertKopecksToRubles(BigDecimal.valueOf(amountInKopecks));
        } catch (Exception e) {
            logger.error("Error calculating amount for user {}: {}", userId, e.getMessage());
            return BigDecimal.ZERO;
        }
    }

    /**
     * Получает информацию о продукте по его идентификатору.
     *
     * @param productId идентификатор продукта
     * @return Optional с информацией о продукте, если найден
     */
    /**
     * Получает информацию о продукте по его идентификатору.
     * <p>
     * <strong>Важно:</strong> В таблице PRODUCTS нет столбца DESCRIPTION,
     * поэтому поле description будет установлено в null.
     * </p>
     *
     * @param productId идентификатор продукта
     * @return Optional с информацией о продукте, если найден
     */
    public Optional<ProductInfo> getProductById(UUID productId) {
        try {
            ProductInfo product = jdbcTemplate.queryForObject(
                    SqlQueries.GET_PRODUCT_BY_ID,
                    new ProductInfoRowMapper(),
                    productId
            );
            return Optional.ofNullable(product);
        } catch (EmptyResultDataAccessException e) {
            logger.debug("Product not found with ID: {}", productId);
            return Optional.empty();
        } catch (Exception e) {
            logger.error("Error getting product by ID {}: {}", productId, e.getMessage());
            return Optional.empty();
        }
    }

    /**
     * Получает список всех продуктов из базы данных.
     * <p>
     * Внимание: в SqlQueries нет запроса для получения всех продуктов,
     * поэтому используем временную реализацию.
     * </p>
     *
     * @return список всех продуктов
     */
    /**
     * Получает список всех продуктов из базы данных.
     * <p>
     * <strong>Важно:</strong> В таблице PRODUCTS нет столбца DESCRIPTION,
     * поэтому поле description будет установлено в null.
     * </p>
     *
     * @return список всех продуктов
     */
    public List<ProductInfo> getAllProducts() {
        try {
            return jdbcTemplate.query(
                    SqlQueries.GET_ALL_PRODUCTS,
                    new ProductInfoRowMapper()
            );
        } catch (Exception e) {
            logger.error("Error getting all products: {}", e.getMessage());
            return List.of();
        }
    }

    /**
     * Получает количество транзакций пользователя по типу продукта.
     *
     * @param userId идентификатор пользователя
     * @param productType тип продукта
     * @return количество транзакций
     */
    public int getTransactionCountByProductType(UUID userId, String productType) {
        try {
            Integer count = jdbcTemplate.queryForObject(
                    SqlQueries.GET_TRANSACTION_COUNT_BY_PRODUCT_TYPE,
                    Integer.class,
                    userId, productType
            );
            return count != null ? count : 0;
        } catch (Exception e) {
            logger.error("Error getting transaction count for user {}: {}", userId, e.getMessage());
            return 0;
        }
    }

    /**
     * Получает общую сумму пополнений пользователя.
     *
     * @param userId идентификатор пользователя
     * @return сумма пополнений в рублях
     */
    public BigDecimal getTotalDeposits(UUID userId) {
        try {
            Integer kopecks = jdbcTemplate.queryForObject(
                    SqlQueries.GET_TOTAL_DEPOSITS,
                    Integer.class,
                    userId
            );
            return convertKopecksToRubles(BigDecimal.valueOf(kopecks != null ? kopecks : 0));
        } catch (Exception e) {
            logger.error("Error getting total deposits for user {}: {}", userId, e.getMessage());
            return BigDecimal.ZERO;
        }
    }

    /**
     * Получает общую сумму трат пользователя.
     *
     * @param userId идентификатор пользователя
     * @return сумма трат в рублях
     */
    public BigDecimal getTotalWithdrawals(UUID userId) {
        try {
            Integer kopecks = jdbcTemplate.queryForObject(
                    SqlQueries.GET_TOTAL_WITHDRAWALS,
                    Integer.class,
                    userId
            );
            return convertKopecksToRubles(BigDecimal.valueOf(kopecks != null ? kopecks : 0));
        } catch (Exception e) {
            logger.error("Error getting total withdrawals for user {}: {}", userId, e.getMessage());
            return BigDecimal.ZERO;
        }
    }

    /**
     * Конвертирует сумму из копеек в рубли.
     * <p>
     * Метод выполняет деление на 100 с округлением до 2 знаков после запятой
     * в соответствии с правилами банковских расчетов.
     * </p>
     *
     * @param kopecks сумма в копейках
     * @return сумма в рублях
     */
    private BigDecimal convertKopecksToRubles(BigDecimal kopecks) {
        if (kopecks == null) {
            return BigDecimal.ZERO;
        }

        // Делим на 100 для конвертации копеек в рубли
        // Используем HALF_UP - стандартное банковское округление
        return kopecks.divide(KOPECKS_IN_RUBLE, 2, RoundingMode.HALF_UP);
    }

    /**
     * RowMapper для преобразования ResultSet в ProductInfo.
     */
    /**
     * RowMapper для преобразования ResultSet в ProductInfo.
     */
    private static class ProductInfoRowMapper implements RowMapper<ProductInfo> {
        @Override
        public ProductInfo mapRow(ResultSet rs, int rowNum) throws SQLException {
            // Получаем ID, NAME, TYPE из запроса
            UUID id = UUID.fromString(rs.getString("ID"));
            String name = rs.getString("NAME");
            String type = rs.getString("TYPE");

            // В таблице PRODUCTS нет столбца DESCRIPTION, поэтому устанавливаем null
            // Описание продуктов берется из констант ProductConstants
            String description = null;

            return new ProductInfo(id, name, type, description);
        }
    }
}