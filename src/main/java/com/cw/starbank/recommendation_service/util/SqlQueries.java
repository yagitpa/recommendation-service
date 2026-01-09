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
 * <h3>Особенности базы данных H2:</h3>
 * <ul>
 *   <li>Имена таблиц и столбцов в верхнем регистре</li>
 *   <li>Использование двойных кавычек для экранирования</li>
 *   <li>Типы транзакций: DEPOSIT (пополнение) и WITHDRAW (снятие)</li>
 * </ul>
 *
 * @see ProductConstants
 * @see com.cw.starbank.recommendation_service.repository.RecommendationRepository
 */
public final class SqlQueries {

    /**
     * SQL запрос для проверки использования продукта определенного типа пользователем.
     */
    public static final String USES_PRODUCT_TYPE =
            "SELECT COUNT(*) > 0 " +
                    "FROM \"TRANSACTIONS\" t " +
                    "JOIN \"PRODUCTS\" p ON t.\"PRODUCT_ID\" = p.\"ID\" " +
                    "WHERE t.\"USER_ID\" = ? AND p.\"TYPE\" = ?";

    /**
     * SQL запрос для расчета суммы операций по типу продукта и типу операции.
     * <p>
     * <strong>Внимание:</strong> Тип транзакции должен быть 'DEPOSIT' или 'WITHDRAW'
     * </p>
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
     * <h4>Примечание:</h4>
     * Столбец DESCRIPTION отсутствует в таблице PRODUCTS.
     */
    public static final String GET_PRODUCT_BY_ID =
            "SELECT \"ID\", \"NAME\", \"TYPE\" " +
                    "FROM \"PRODUCTS\" WHERE \"ID\" = ?";

    /**
     * SQL запрос для получения общей суммы операций по типу продукта.
     */
    public static final String GET_TOTAL_AMOUNT_BY_PRODUCT_TYPE =
            "SELECT COALESCE(SUM(t.\"AMOUNT\"), 0) " +
                    "FROM \"TRANSACTIONS\" t " +
                    "JOIN \"PRODUCTS\" p ON t.\"PRODUCT_ID\" = p.\"ID\" " +
                    "WHERE t.\"USER_ID\" = ? AND p.\"TYPE\" = ?";

    /**
     * SQL запрос для проверки существования пользователя.
     */
    public static final String USER_EXISTS =
            "SELECT COUNT(*) > 0 FROM \"USERS\" WHERE \"ID\" = ?";

    /**
     * SQL запрос для получения общей суммы пополнений пользователя.
     * <p>
     * <strong>Тип транзакции:</strong> 'DEPOSIT'
     * </p>
     */
    public static final String GET_TOTAL_DEPOSITS =
            "SELECT COALESCE(SUM(t.\"AMOUNT\"), 0) " +
                    "FROM \"TRANSACTIONS\" t " +
                    "WHERE t.\"USER_ID\" = ? AND t.\"TYPE\" = 'DEPOSIT'";

    /**
     * SQL запрос для получения общей суммы трат пользователя.
     * <p>
     * <strong>Тип транзакции:</strong> 'WITHDRAW' (без 'AL' на конце!)
     * </p>
     */
    public static final String GET_TOTAL_WITHDRAWALS =
            "SELECT COALESCE(SUM(t.\"AMOUNT\"), 0) " +
                    "FROM \"TRANSACTIONS\" t " +
                    "WHERE t.\"USER_ID\" = ? AND t.\"TYPE\" = 'WITHDRAW'"; // ИСПРАВЛЕНО: WITHDRAW вместо WITHDRAWAL

    /**
     * SQL запрос для получения количества транзакций по типу продукта.
     */
    public static final String GET_TRANSACTION_COUNT_BY_PRODUCT_TYPE =
            "SELECT COUNT(*) " +
                    "FROM \"TRANSACTIONS\" t " +
                    "JOIN \"PRODUCTS\" p ON t.\"PRODUCT_ID\" = p.\"ID\" " +
                    "WHERE t.\"USER_ID\" = ? AND p.\"TYPE\" = ?";

    /**
     * SQL запрос для получения списка продуктов, используемых пользователем.
     */
    public static final String GET_USER_PRODUCTS =
            "SELECT DISTINCT p.\"ID\", p.\"NAME\", p.\"TYPE\" " +
                    "FROM \"TRANSACTIONS\" t " +
                    "JOIN \"PRODUCTS\" p ON t.\"PRODUCT_ID\" = p.\"ID\" " +
                    "WHERE t.\"USER_ID\" = ? " +
                    "ORDER BY p.\"TYPE\", p.\"NAME\"";

    /**
     * SQL запрос для получения всех транзакций пользователя с группировкой по типам продуктов.
     * <p>
     * <strong>Типы транзакций:</strong> 'DEPOSIT' и 'WITHDRAW'
     * </p>
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
     * <p>
     * Возвращает все продукты из базы данных.
     * </p>
     *
     * <h4>Примечание:</h4>
     * Столбец DESCRIPTION отсутствует в таблице PRODUCTS.
     */
    public static final String GET_ALL_PRODUCTS =
            "SELECT \"ID\", \"NAME\", \"TYPE\" " +
                    "FROM \"PRODUCTS\" ORDER BY \"NAME\"";

    /**
     * SQL запрос для простой проверки подключения к базе данных.
     */
    public static final String CHECK_CONNECTION =
            "SELECT 1";

    /**
     * Приватный конструктор для предотвращения инстанцирования утилитного класса.
     */
    private SqlQueries() {
        throw new UnsupportedOperationException("Utility class cannot be instantiated");
    }
}