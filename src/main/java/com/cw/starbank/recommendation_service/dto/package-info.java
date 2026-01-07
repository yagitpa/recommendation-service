/**
 * Пакет dto содержит Data Transfer Objects (DTO) для передачи данных между слоями приложения.
 * <p>
 * DTO классы представляют собой простые объекты Java, которые используются для
 * передачи данных между различными слоями приложения (Controller, Service, Repository)
 * и клиентами REST API. Они не содержат бизнес-логики, только данные.
 * </p>
 *
 * <h2>Основные классы:</h2>
 * <dl>
 *   <dt>{@link com.cw.starbank.recommendation_service.dto.RecommendationDTO}</dt>
 *   <dd>Основной DTO для ответа с рекомендациями продуктов.
 *       Соответствует формату, указанному в техническом задании.
 *       Содержит идентификатор пользователя и список рекомендаций.</dd>
 *
 *   <dt>{@link com.cw.starbank.recommendation_service.dto.RecommendationDTO.ProductRecommendation}</dt>
 *   <dd>Вложенный DTO для информации об отдельном рекомендованном продукте.
 *       Содержит идентификатор, название и описание продукта.</dd>
 *
 *   <dt>{@link com.cw.starbank.recommendation_service.dto.ProductInfo}</dt>
 *   <dd>DTO для хранения информации о банковском продукте из базы данных.
 *       Используется для передачи данных между репозиторием и бизнес-правилами.
 *       Содержит основные атрибуты продукта и описание.</dd>
 * </dl>
 *
 * <h2>Формат JSON ответов API:</h2>
 * <pre>
 * {
 *   "user_id": "cd515076-5d8a-44be-930e-8d4fcb79f42d",
 *   "recommendations": [
 *     {
 *       "name": "Invest 500",
 *       "id": "147f6a0f-3b91-413b-ab99-87f081d60d5a",
 *       "text": "Описание продукта..."
 *     }
 *   ]
 * }
 * </pre>
 *
 * <h2>Особенности реализации:</h2>
 * <ul>
 *   <li>Использование Lombok аннотаций для автоматической генерации геттеров, сеттеров и конструкторов</li>
 *   <li>Сериализация в JSON через Spring MVC (Jackson)</li>
 *   <li>Аннотации Swagger ({@link io.swagger.v3.oas.annotations.media.Schema}) для документации API</li>
 *   <li>Неизменяемость объектов через final поля и конструкторы</li>
 *   <li>Поддержка {@link java.util.Optional} для безопасной работы с nullable полями</li>
 * </ul>
 *
 * <h2>Назначение DTO:</h2>
 * <ul>
 *   <li>Изоляция внутренней модели данных от внешнего API</li>
 *   <li>Контроль над данными, экспортируемыми через API</li>
 *   <li>Упрощение сериализации/десериализации JSON</li>
 *   <li>Предоставление стабильного контракта API для клиентов</li>
 *   <li>Валидация данных на уровне DTO (будущее расширение)</li>
 * </ul>
 *
 * @see com.fasterxml.jackson.annotation.JsonProperty
 * @see lombok.Data
 * @see io.swagger.v3.oas.annotations.media.Schema
 * @since 1.0.0
 */
package com.cw.starbank.recommendation_service.dto;