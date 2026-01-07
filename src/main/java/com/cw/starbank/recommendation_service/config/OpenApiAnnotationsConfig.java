package com.cw.starbank.recommendation_service.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import io.swagger.v3.oas.annotations.servers.Server;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * Глобальные аннотации OpenAPI для всего приложения.
 * <p>
 * Дополняет конфигурацию {@link OpenApiConfig} аннотационным подходом.
 * Позволяет определить общие теги, которые будут использоваться во всех контроллерах.
 * </p>
 *
 * @see OpenApiConfig
 * @since 1.0.0
 */
@OpenAPIDefinition(
        info = @Info(
                title = "Bank Recommendation Service API",
                version = "1.0.0",
                description = "Сервис рекомендаций банковских продуктов",
                contact = @Contact(
                        name = "Bank Star Development Team",
                        email = "dev@starbank.ru",
                        url = "https://starbank.ru"
                ),
                license = @License(
                        name = "Proprietary",
                        url = "https://starbank.ru/license"
                )
        ),
        servers = {
                @Server(
                        url = "http://localhost:8080/api",
                        description = "Локальный сервер разработки"
                ),
                @Server(
                        url = "https://api.starbank.ru/recommendation-service",
                        description = "Продовый сервер"
                )
        },
        tags = {
                @Tag(
                        name = "Recommendation API",
                        description = "API для получения рекомендаций банковских продуктов"
                ),
                @Tag(
                        name = "Technical Specification Products",
                        description = "API для работы с продуктами из технического задания"
                ),
                @Tag(
                        name = "Debug",
                        description = "Эндпойнты для отладки и диагностики"
                )
        }
)
public class OpenApiAnnotationsConfig {
    // Этот класс служит только для хранения аннотаций OpenAPI
}