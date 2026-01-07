package com.cw.starbank.recommendation_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Основной класс Spring Boot приложения для сервиса рекомендаций.
 * <p>
 * Приложение предоставляет REST API для получения персонализированных
 * рекомендаций банковских продуктов на основе транзакционной истории клиентов.
 * </p>
 *
 * <h3>Основные функции:</h3>
 * <ul>
 *   <li>Обработка REST запросов для получения рекомендаций</li>
 *   <li>Проверка бизнес-правил для рекомендаций продуктов</li>
 *   <li>Взаимодействие с базой данных через JdbcTemplate</li>
 *   <li>Предоставление Swagger документации API</li>
 * </ul>
 *
 * @author Банк "Стар", Команда разработки
 * @version 1.0.0
 * @see com.cw.starbank.recommendation_service.controller.RecommendationController
 * @see com.cw.starbank.recommendation_service.service.RecommendationService
 * @since january 2026
 */
@SpringBootApplication
public class RecommendationServiceApplication {

    /**
     * Точка входа в приложение Spring Boot.
     * <p>
     * Запускает встроенный контейнер сервлетов и настраивает
     * Spring ApplicationContext.
     * </p>
     *
     * @param args аргументы командной строки
     */
    public static void main(String[] args) {
        SpringApplication.run(RecommendationServiceApplication.class, args);
    }
}
