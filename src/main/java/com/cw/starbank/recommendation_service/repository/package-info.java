/**
 * Пакет repository содержит классы для доступа к данным приложения.
 * <p>
 * Основной компонент пакета -
 * {@link com.cw.starbank.recommendation_service.repository.RecommendationRepository},
 * который обеспечивает взаимодействие с базой данных через Spring JdbcTemplate.
 * </p>
 *
 * <h2>Ключевые особенности:</h2>
 * <ul>
 *   <li>Использование JdbcTemplate для read-only доступа к существующей БД</li>
 *   <li>Централизованное хранение SQL запросов в {@link com.cw.starbank.recommendation_service.util.SqlQueries}</li>
 *   <li>Безопасная обработка исключений и возврат значений по умолчанию</li>
 *   <li>Поддержка агрегированных запросов для оптимизации производительности</li>
 * </ul>
 *
 * <h2>Основные функции репозитория:</h2>
 * <ul>
 *   <li>Проверка существования пользователей в системе</li>
 *   <li>Получение агрегированных данных о транзакциях пользователя</li>
 *   <li>Предоставление данных для проверки бизнес-правил рекомендаций</li>
 * </ul>
 *
 * <h2>Особенности реализации:</h2>
 * <ul>
 *   <li>Использование prepared statements для защиты от SQL-инъекций</li>
 *   <li>Обработка {@link org.springframework.dao.EmptyResultDataAccessException}</li>
 *   <li>Конвертация сумм из копеек в рубли ({@link java.math.BigDecimal})</li>
 *   <li>Возврат безопасных значений по умолчанию при отсутствии данных</li>
 * </ul>
 *
 * @see com.cw.starbank.recommendation_service.util.SqlQueries
 * @see org.springframework.jdbc.core.JdbcTemplate
 * @since 1.0.0
 */
package com.cw.starbank.recommendation_service.repository;