/**
 * Пакет config содержит классы конфигурации Spring приложения.
 * <p>
 * Классы в этом пакете отвечают за настройку различных аспектов приложения,
 * включая конфигурацию базы данных, Swagger документацию, CORS политики
 * и другие системные настройки.
 * </p>
 *
 * <h2>Основные классы:</h2>
 * <dl>
 *   <dt>{@link com.cw.starbank.recommendation_service.config.OpenApiConfig}</dt>
 *   <dd>Конфигурация Swagger/OpenAPI для генерации документации REST API.
 *       Настраивает метаданные API, информацию о контактах, лицензии и серверы.</dd>
 *
 *   <dt>{@link com.cw.starbank.recommendation_service.config.WebConfig}</dt>
 *   <dd>Конфигурация CORS (Cross-Origin Resource Sharing) для веб-приложения.
 *       Настраивает политики CORS для разрешения кросс-доменных запросов от
 *       указанных источников во время разработки и тестирования.</dd>
 * </dl>
 *
 * <h2>Конфигурационные файлы:</h2>
 * <ul>
 *   <li><code>application.yml</code> - Основные настройки приложения</li>
 * </ul>
 *
 * <h2>Ключевые настройки:</h2>
 * <ul>
 *   <li>Порт сервера и контекстный путь</li>
 *   <li>Подключение к базе данных H2</li>
 *   <li>Настройки пула соединений HikariCP</li>
 *   <li>Параметры логирования</li>
 *   <li>Swagger/OpenAPI конфигурация</li>
 *   <li>CORS политики</li>
 * </ul>
 *
 * @see org.springframework.context.annotation.Configuration
 * @see org.springframework.boot.autoconfigure.SpringBootApplication
 * @since 1.0.0
 */
package com.cw.starbank.recommendation_service.config;