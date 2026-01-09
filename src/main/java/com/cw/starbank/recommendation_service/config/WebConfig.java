package com.cw.starbank.recommendation_service.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Конфигурация CORS (Cross-Origin Resource Sharing) для веб-приложения.
 * <p>
 * Настраивает политики CORS для разрешения кросс-доменных запросов
 * от указанных источников во время разработки и тестирования.
 * </p>
 *
 * <h3>Настраиваемые параметры:</h3>
 * <ul>
 *   <li>Разрешенные источники (origins)</li>
 *   <li>Разрешенные HTTP методы</li>
 *   <li>Разрешенные заголовки</li>
 *   <li>Поддержка учетных данных</li>
 *   <li>Время кэширования preflight запросов</li>
 * </ul>
 *
 * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/CORS">MDN CORS Documentation</a>
 */
@Configuration
public class WebConfig {

    @Value("${cors.allowed-origins:http://localhost:3000,http://localhost:8080}")
    private String[] allowedOrigins;

    @Value("${cors.allowed-methods:GET,POST,PUT,DELETE,OPTIONS}")
    private String[] allowedMethods;

    @Value("${cors.allowed-headers:*}")
    private String[] allowedHeaders;

    /**
     * Создает конфигуратор CORS для Spring MVC.
     * <p>
     * Настраивает политики CORS для всех эндпойнтов, начинающихся с {@code /api/}.
     * </p>
     *
     * @return конфигуратор WebMvcConfigurer с настройками CORS
     */
    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/api/**")
                        .allowedOrigins(allowedOrigins)
                        .allowedMethods(allowedMethods)
                        .allowedHeaders(allowedHeaders)
                        .allowCredentials(true)
                        .maxAge(3600);
            }
        };
    }
}