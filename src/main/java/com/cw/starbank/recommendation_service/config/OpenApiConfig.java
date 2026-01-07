package com.cw.starbank.recommendation_service.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * Конфигурация Swagger/OpenAPI для генерации документации REST API.
 * <p>
 * Настраивает метаданные API, информацию о контактах, лицензии
 * и серверах для Swagger UI.
 * </p>
 *
 * <h3>Доступные эндпойнты документации:</h3>
 * <ul>
 *   <li>Swagger UI: {@code /api/swagger-ui.html}</li>
 *   <li>OpenAPI спецификация: {@code /api/api-docs}</li>
 * </ul>
 *
 * <h3>Основные группы эндпойнтов:</h3>
 * <ul>
 *   <li><strong>Recommendation API</strong> - получение рекомендаций продуктов</li>
 *   <li><strong>Technical Specification Products</strong> - работа с продуктами из ТЗ</li>
 * </ul>
 *
 * @see <a href="https://swagger.io/specification/">OpenAPI Specification</a>
 */
@Configuration
public class OpenApiConfig {

    @Value("${server.port:8080}")
    private String serverPort;

    /**
     * Создает и настраивает объект OpenAPI для документации.
     * <p>
     * Определяет основную информацию о API, контакты разработчиков,
     * лицензию и доступные серверы.
     * </p>
     *
     * @return сконфигурированный объект OpenAPI
     */
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Bank Recommendation Service API")
                        .version("1.0.0")
                        .description("""
                                Сервис рекомендаций банковских продуктов для клиентов банка "Стар".
                                
                                ### Основные возможности:
                                - Получение персонализированных рекомендаций продуктов
                                - Проверка соответствия пользователя бизнес-правилам
                                - Тестирование на предопределенных пользователях
                                - Отладка и проверка корректности работы системы
                                
                                ### Доступные API группы:
                                1. **Recommendation API** (`/api/recommendation/**`)
                                   - `GET /{userId}` - получение рекомендаций
                                   - `GET /{userId}/statistics` - статистика по пользователю
                                   - `GET /test` - тестирование на тестовых пользователях
                                
                                2. **Technical Specification Products** (`/api/tz-products/**`)
                                   - `GET /` - список продуктов из ТЗ
                                   - `GET /check` - проверка настроек продуктов
                                
                                ### Тестовые пользователи:
                                1. `cd515076-5d8a-44be-930e-8d4fcb79f42d` - для проверки Invest 500
                                2. `d4a4d619-9a0c-4fc5-b0cb-76c49409546b` - для проверки Top Saving
                                3. `1f9b149c-6577-448a-bc94-16bea229b71a` - для проверки Простого кредита
                                
                                ### Форматы данных:
                                - Все денежные суммы передаются в рублях
                                - Идентификаторы используются в формате UUID
                                - Даты и время в формате ISO 8601
                                """)
                        .contact(new Contact()
                                .name("Bank Star Development Team")
                                .email("dev@starbank.ru")
                                .url("https://starbank.ru"))
                        .license(new License()
                                .name("Proprietary")
                                .url("https://starbank.ru/license"))
                        .termsOfService("https://starbank.ru/terms"))
                .servers(List.of(
                        new Server()
                                .url("http://localhost:" + serverPort + "/api")
                                .description("Локальный сервер разработки"),
                        new Server()
                                .url("https://api.starbank.ru/recommendation-service")
                                .description("Продовый сервер")
                ));
    }
}