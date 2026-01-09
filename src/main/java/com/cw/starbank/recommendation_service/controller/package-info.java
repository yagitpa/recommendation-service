/**
 * Пакет controller содержит REST контроллеры для обработки HTTP запросов.
 * <p>
 * Контроллеры в этом пакете реализуют REST API интерфейс сервиса рекомендаций,
 * обрабатывают входящие HTTP запросы, валидируют параметры и возвращают
 * ответы в формате JSON.
 * </p>
 *
 * <h2>Основные классы:</h2>
 * <dl>
 *   <dt>{@link com.cw.starbank.recommendation_service.controller.RecommendationController}</dt>
 *   <dd>Главный REST контроллер для обработки запросов рекомендаций.
 *       Предоставляет эндпойнты для получения рекомендаций продуктов,
 *       статистики по пользователям и тестирования функциональности.</dd>
 * </dl>
 *
 * <h2>Предоставляемые REST API:</h2>
 * <ul>
 *   <li><code>GET /api/recommendation/{userId}</code> - Получение рекомендаций для пользователя</li>
 *   <li><code>GET /api/recommendation/{userId}/statistics</code> - Статистика по пользователю</li>
 *   <li><code>GET /api/recommendation/test</code> - Тестирование на предопределенных пользователях</li>
 * </ul>
 *
 * <h2>Особенности реализации:</h2>
 * <ul>
 *   <li>Использование аннотаций Spring MVC (<code>@RestController</code>, <code>@GetMapping</code>)</li>
 *   <li>Валидация входных параметров (формат UUID)</li>
 *   <li>Обработка исключений с возвратом соответствующих HTTP статусов</li>
 *   <li>Полная Swagger документация всех эндпойнтов</li>
 *   <li>Логирование всех входящих запросов и ошибок</li>
 *   <li>Поддержка CORS для кросс-доменных запросов</li>
 * </ul>
 *
 * <h2>Формат ответов:</h2>
 * <ul>
 *   <li>Успешный ответ: HTTP 200 с JSON телом</li>
 *   <li>Ошибка валидации: HTTP 400</li>
 *   <li>Внутренняя ошибка: HTTP 500</li>
 *   <li>Пустой массив рекомендаций при отсутствии подходящих продуктов</li>
 * </ul>
 *
 * @see org.springframework.web.bind.annotation.RestController
 * @see org.springframework.web.bind.annotation.GetMapping
 * @see io.swagger.v3.oas.annotations.tags.Tag
 * @since 1.0.0
 */
package com.cw.starbank.recommendation_service.controller;