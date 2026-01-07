/**
 * Пакет recommendation_service содержит основную логику сервиса рекомендаций банковских продуктов.
 * <p>
 * Сервис предоставляет REST API для получения персонализированных рекомендаций
 * банковских продуктов на основе транзакционной истории клиентов банка "Стар".
 * </p>
 *
 * <h2>Архитектурные принципы:</h2>
 * <ul>
 *   <li>Многослойная архитектура (Controller-Service-Repository)</li>
 *   <li>Четкое разделение ответственности между компонентами</li>
 *   <li>Инверсия зависимостей через Spring Dependency Injection</li>
 *   <li>Read-only доступ к существующей базе данных транзакций</li>
 *   <li>Использование агрегированных данных для оптимизации производительности</li>
 * </ul>
 *
 * <h2>Основные модули:</h2>
 * <ul>
 *   <li>{@link com.cw.starbank.recommendation_service.config} - Конфигурационные классы</li>
 *   <li>{@link com.cw.starbank.recommendation_service.controller} - REST контроллеры</li>
 *   <li>{@link com.cw.starbank.recommendation_service.service} - Сервисный слой бизнес-логики</li>
 *   <li>{@link com.cw.starbank.recommendation_service.repository} - Доступ к данным</li>
 *   <li>{@link com.cw.starbank.recommendation_service.aggregator} - Агрегация транзакционных данных</li>
 *   <li>{@link com.cw.starbank.recommendation_service.dto} - Data Transfer Objects</li>
 *   <li>{@link com.cw.starbank.recommendation_service.util} - Утилитные классы и константы</li>
 * </ul>
 *
 * <h2>Ключевые возможности:</h2>
 * <ul>
 *   <li>Анализ транзакционной истории пользователей</li>
 *   <li>Агрегация транзакций по типам продуктов (DEBIT, INVEST, SAVING, CREDIT)</li>
 *   <li>Применение бизнес-правил для рекомендаций продуктов</li>
 *   <li>Интеграция с существующей базой данных банка (read-only)</li>
 *   <li>Предоставление REST API с Swagger документацией</li>
 *   <li>Поддержка тестовых пользователей для отладки</li>
 * </ul>
 *
 * <h2>Рабочий процесс:</h2>
 * <ol>
 *   <li>Получение запроса с ID пользователя через REST API</li>
 *   <li>Проверка существования пользователя в системе</li>
 *   <li>Агрегация всех транзакций пользователя по типам продуктов</li>
 *   <li>Проверка бизнес-правил на основе агрегированных данных</li>
 *   <li>Формирование рекомендаций из продуктов технического задания</li>
 *   <li>Возврат результатов в формате JSON</li>
 * </ol>
 *
 * <h2>Основные классы:</h2>
 * <dl>
 *   <dt>{@link com.cw.starbank.recommendation_service.RecommendationServiceApplication}</dt>
 *   <dd>Основной класс Spring Boot приложения</dd>
 *
 *   <dt>{@link com.cw.starbank.recommendation_service.controller.RecommendationController}</dt>
 *   <dd>REST контроллер для обработки запросов рекомендаций</dd>
 *
 *   <dt>{@link com.cw.starbank.recommendation_service.service.RecommendationService}</dt>
 *   <dd>Основной сервис для оркестрации процесса рекомендаций</dd>
 *
 *   <dt>{@link com.cw.starbank.recommendation_service.service.RuleEngineService}</dt>
 *   <dd>Сервис для проверки бизнес-правил рекомендаций</dd>
 *
 *   <dt>{@link com.cw.starbank.recommendation_service.aggregator.UserTransactionAggregator}</dt>
 *   <dd>Компонент для агрегации транзакций пользователя по типам продуктов</dd>
 *
 *   <dt>{@link com.cw.starbank.recommendation_service.repository.RecommendationRepository}</dt>
 *   <dd>Репозиторий для доступа к данным транзакций</dd>
 * </dl>
 *
 * <h2>Бизнес-правила рекомендаций (из технического задания):</h2>
 *
 * <h3>Invest 500:</h3>
 * <ol>
 *   <li>Пользователь использует как минимум один продукт с типом DEBIT</li>
 *   <li>Пользователь не использует продукты с типом INVEST</li>
 *   <li>Сумма пополнений продуктов с типом SAVING больше 1000 ₽</li>
 * </ol>
 *
 * <h3>Top Saving:</h3>
 * <ol>
 *   <li>Пользователь использует как минимум один продукт с типом DEBIT</li>
 *   <li>Сумма пополнений по всем продуктам типа DEBIT >= 50000 ₽ ИЛИ
 *       Сумма пополнений по всем продуктам типа SAVING >= 50000 ₽</li>
 *   <li>Сумма пополнений по всем продуктам типа DEBIT > суммы трат по всем продуктам типа DEBIT</li>
 * </ol>
 *
 * <h3>Простой кредит:</h3>
 * <ol>
 *   <li>Пользователь не использует продукты с типом CREDIT</li>
 *   <li>Сумма пополнений по всем продуктам типа DEBIT > суммы трат по всем продуктам типа DEBIT</li>
 *   <li>Сумма трат по всем продуктам типа DEBIT > 100000 ₽</li>
 * </ol>
 *
 * <h2>Важные особенности реализации:</h2>
 * <ul>
 *   <li>Продукты для рекомендаций берутся исключительно из технического задания</li>
 *   <li>База данных используется только для проверки условий правил</li>
 *   <li>Все суммы конвертируются из копеек в рубли</li>
 *   <li>Агрегация данных выполняется за один SQL-запрос для оптимизации</li>
 *   <li>Поддерживаются multiple рекомендации (пользователь может получить несколько продуктов)</li>
 * </ul>
 *
 * @see <a href="https://github.com/yagitpa/recommendation-service">GitHub репозиторий</a>
 * @since 1.0.0
 * @version 1.0.0
 */
package com.cw.starbank.recommendation_service;