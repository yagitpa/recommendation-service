/**
 * Пакет aggregator содержит классы для агрегации и анализа транзакционных данных пользователя.
 * <p>
 * Основной класс {@link com.cw.starbank.recommendation_service.aggregator.UserTransactionAggregator}
 * предназначен для сбора и группировки всех транзакций пользователя по типам продуктов.
 * Это позволяет эффективно проверять бизнес-правила рекомендаций на основе агрегированных данных.
 * </p>
 *
 * <h2>Назначение:</h2>
 * <ul>
 *   <li>Агрегация всех транзакций пользователя в единую структуру данных</li>
 *   <li>Группировка транзакций по типам продуктов (DEBIT, INVEST, SAVING, CREDIT)</li>
 *   <li>Разделение транзакций на DEPOSIT (пополнения) и WITHDRAWAL (траты)</li>
 *   <li>Предоставление удобного API для проверки бизнес-правил</li>
 * </ul>
 *
 * <h2>Принцип работы:</h2>
 * <p>
 * Aggregator собирает данные из результата SQL-запроса, который возвращает все транзакции
 * пользователя с указанием типа продукта и типа транзакции. Данные автоматически
 * конвертируются из копеек в рубли и агрегируются в памяти для быстрого доступа.
 * </p>
 *
 * <h2>Взаимодействие с другими компонентами:</h2>
 * <ul>
 *   <li>Используется {@link com.cw.starbank.recommendation_service.repository.RecommendationRepository}
 *       для получения сырых данных транзакций</li>
 *   <li>Предоставляет данные {@link com.cw.starbank.recommendation_service.service.RuleEngineService}
 *       для проверки бизнес-правил</li>
 *   <li>Интегрируется с {@link com.cw.starbank.recommendation_service.service.RecommendationService}
 *       для формирования рекомендаций</li>
 * </ul>
 *
 * @since 1.0.0
 */
package com.cw.starbank.recommendation_service.aggregator;