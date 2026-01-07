package com.cw.starbank.recommendation_service.util;

/**
 * Утилитный класс со всеми SQL запросами, используемыми в приложении.
 * <p>
 * Централизованное хранилище SQL запросов для обеспечения:
 * </p>
 * <ul>
 *   <li>Единого источника истины для всех SQL операций</li>
 *   <li>Легкой поддержки и модификации запросов</li>
 *   <li>Упрощения тестирования и отладки</li>
 *   <li>Исключения SQL-инъекций через использование prepared statements</li>
 * </ul>
 *
 * <h3>Структура запросов:</h3>
 * <ul>
 *   <li>Используются именованные константы для читаемости</li>
 *   <li>Все запросы совместимы с H2 Database</li>
 *   <li>Используются JOIN для связи таблиц</li>
 *   <li>Применяется COALESCE для обработки NULL значений</li>
 * </ul>
 *
 * @see ProductConstants
 * @see com.cw.starbank.recommendation_service.repository.RecommendationRepository
 */
public final class SqlQueries {

    /**
     * SQL запрос для проверки использования продукта определенного типа пользователем.
     * <p>
     * Возвращает {@code true}, если существует хотя бы одна транзакция
     * пользователя по продукту указанного типа.
     * </p>
     *
     * <h4>Параметры:</h4>
     * <ol>
     *   <li>userId - UUID пользователя</li>
     *   <li>productType - тип продукта (DEBIT, INVEST, SAVING, CREDIT)</li>
     * </ol>
     *
     * <h4>Таблицы:</h4>
     * <ul>
     *   <li>TRANSACTIONS (алиас t)</li>
     *   <li>PRODUCTS (алиас p)</li>
     * </ul>
     */
    public static final String USES_PRODUCT_TYPE =
            "SELECT COUNT(*) > 0 " +
                    "FROM \"TRANSACTIONS\" t " +
                    "JOIN \"PRODUCTS\" p ON t.\"PRODUCT_ID\" = p.\"ID\" " +
                    "WHERE t.\"USER_ID\" = ? AND p.\"TYPE\" = ?";
    /**
     * SQL запрос для расчета суммы операций по типу продукта и типу операции.
     * <p>
     * Возвращает сумму операций (в копейках) или 0 при отсутствии операций.
     * </p>
     *
     * <h4>Параметры:</h4>
     * <ol>
     *   <li>userId - UUID пользователя</li>
     *   <li>productType - тип продукта</li>
     *   <li>transactionType - тип операции (DEPOSIT/WITHDRAWAL)</li>
     * </ol>
     */
    public static final String CALCULATE_AMOUNT_BY_TYPE =
            "SELECT COALESCE(SUM(t.\"AMOUNT\"), 0) " +
                    "FROM \"TRANSACTIONS\" t " +
                    "JOIN \"PRODUCTS\" p ON t.\"PRODUCT_ID\" = p.\"ID\" " +
                    "WHERE t.\"USER_ID\" = ? AND p.\"TYPE\" = ? AND t.\"TYPE\" = ?";
    /**
     * SQL запрос для получения информации о продукте по ID.
     * <p>
     * Возвращает основные атрибуты продукта: ID, NAME, TYPE.
     * </p>
     *
     * <h4>Параметры:</h4>
     * <ol>
     *   <li>productId - UUID продукта</li>
     * </ol>
     *
     * <h4>Таблицы:</h4>
     * <ul>
     *   <li>PRODUCTS</li>
     * </ul>
     */
    public static final String GET_PRODUCT_BY_ID =
            "SELECT \"ID\", \"NAME\", \"TYPE\" " +
                    "FROM \"PRODUCTS\" WHERE \"ID\" = ?";
    /**
     * SQL запрос для получения общей суммы операций по типу продукта.
     * <p>
     * Возвращает сумму всех операций (DEPOSIT + WITHDRAWAL) в копейках.
     * </p>
     *
     * <h4>Параметры:</h4>
     * <ol>
     *   <li>userId - UUID пользователя</li>
     *   <li>productType - тип продукта</li>
     * </ol>
     */
    public static final String GET_TOTAL_AMOUNT_BY_PRODUCT_TYPE =
            "SELECT COALESCE(SUM(t.\"AMOUNT\"), 0) " +
                    "FROM \"TRANSACTIONS\" t " +
                    "JOIN \"PRODUCTS\" p ON t.\"PRODUCT_ID\" = p.\"ID\" " +
                    "WHERE t.\"USER_ID\" = ? AND p.\"TYPE\" = ?";
    /**
     * SQL запрос для проверки существования пользователя.
     * <p>
     * Возвращает {@code true}, если пользователь с указанным ID существует.
     * </p>
     *
     * <h4>Параметры:</h4>
     * <ol>
     *   <li>userId - UUID пользователя</li>
     * </ol>
     *
     * <h4>Таблицы:</h4>
     * <ul>
     *   <li>USERS</li>
     * </ul>
     */
    public static final String USER_EXISTS =
            "SELECT COUNT(*) > 0 FROM \"USERS\" WHERE \"ID\" = ?";
    /**
     * SQL запрос для получения общей суммы пополнений пользователя.
     * <p>
     * Возвращает сумму всех DEPOSIT операций пользователя по всем продуктам.
     * </p>
     *
     * <h4>Параметры:</h4>
     * <ol>
     *   <li>userId - UUID пользователя</li>
     * </ol>
     */
    public static final String GET_TOTAL_DEPOSITS =
            "SELECT COALESCE(SUM(t.\"AMOUNT\"), 0) " +
                    "FROM \"TRANSACTIONS\" t " +
                    "WHERE t.\"USER_ID\" = ? AND t.\"TYPE\" = 'DEPOSIT'";
    /**
     * SQL запрос для получения общей суммы трат пользователя.
     * <p>
     * Возвращает сумму всех WITHDRAWAL операций пользователя по всем продуктам.
     * </p>
     *
     * <h4>Параметры:</h4>
     * <ol>
     *   <li>userId - UUID пользователя</li>
     * </ol>
     */
    public static final String GET_TOTAL_WITHDRAWALS =
            "SELECT COALESCE(SUM(t.\"AMOUNT\"), 0) " +
                    "FROM \"TRANSACTIONS\" t " +
                    "WHERE t.\"USER_ID\" = ? AND t.\"TYPE\" = 'WITHDRAWAL'";
    /**
     * SQL запрос для получения количества транзакций по типу продукта.
     * <p>
     * Возвращает общее количество транзакций пользователя по продуктам
     * указанного типа.
     * </p>
     *
     * <h4>Параметры:</h4>
     * <ol>
     *   <li>userId - UUID пользователя</li>
     *   <li>productType - тип продукта</li>
     * </ol>
     */
    public static final String GET_TRANSACTION_COUNT_BY_PRODUCT_TYPE =
            "SELECT COUNT(*) " +
                    "FROM \"TRANSACTIONS\" t " +
                    "JOIN \"PRODUCTS\" p ON t.\"PRODUCT_ID\" = p.\"ID\" " +
                    "WHERE t.\"USER_ID\" = ? AND p.\"TYPE\" = ?";
    /**
     * SQL запрос для получения списка продуктов, используемых пользователем.
     * <p>
     * Возвращает уникальные продукты, по которым у пользователя есть транзакции.
     * Результаты сортируются по типу и названию продукта.
     * </p>
     *
     * <h4>Параметры:</h4>
     * <ol>
     *   <li>userId - UUID пользователя</li>
     * </ol>
     */
    public static final String GET_USER_PRODUCTS =
            "SELECT DISTINCT p.\"ID\", p.\"NAME\", p.\"TYPE\" " +
                    "FROM \"TRANSACTIONS\" t " +
                    "JOIN \"PRODUCTS\" p ON t.\"PRODUCT_ID\" = p.\"ID\" " +
                    "WHERE t.\"USER_ID\" = ? " +
                    "ORDER BY p.\"TYPE\", p.\"NAME\"";

    /**
     * Приватный конструктор для предотвращения инстанцирования утилитного класса.
     *
     * @throws UnsupportedOperationException всегда
     */
    private SqlQueries() {
        throw new UnsupportedOperationException("Utility class cannot be instantiated");
    }

    /**
     * SQL запрос для получения всех транзакций пользователя с группировкой по типам продуктов.
     * Используется для агрегации данных пользователя для проверки правил рекомендаций.
     *
     * <h4>Параметры:</h4>
     * <ol>
     *   <li>userId - UUID пользователя</li>
     * </ol>
     *
     * <h4>Возвращает:</h4>
     * <ul>
     *   <li>product_type - тип продукта (DEBIT, INVEST, SAVING, CREDIT)</li>
     *   <li>transaction_type - тип транзакции (DEPOSIT, WITHDRAWAL)</li>
     *   <li>amount - сумма транзакции в копейках</li>
     * </ul>
     */
    public static final String GET_USER_TRANSACTIONS_BY_PRODUCT_TYPE =
            "SELECT p.\"TYPE\" as product_type, " +
                    "t.\"TYPE\" as transaction_type, " +
                    "t.\"AMOUNT\" as amount " +
                    "FROM \"TRANSACTIONS\" t " +
                    "JOIN \"PRODUCTS\" p ON t.\"PRODUCT_ID\" = p.\"ID\" " +
                    "WHERE t.\"USER_ID\" = ? " +
                    "ORDER BY p.\"TYPE\", t.\"TYPE\"";

    /**
     * SQL запрос для получения всех продуктов.
     */
    public static final String GET_ALL_PRODUCTS =
            "SELECT \"ID\", \"NAME\", \"TYPE\", \"DESCRIPTION\" " +
                    "FROM \"PRODUCTS\" ORDER BY \"NAME\"";

    /**
     * SQL запрос для простой проверки подключения к базе данных.
     */
    public static final String CHECK_CONNECTION =
            "SELECT 1 FROM DUAL";
}
