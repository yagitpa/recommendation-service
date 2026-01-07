package com.cw.starbank.recommendation_service.controller;

import com.cw.starbank.recommendation_service.dto.RecommendationDTO;
import com.cw.starbank.recommendation_service.service.RecommendationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

/**
 * REST контроллер для обработки запросов рекомендаций
 */
@RestController
@RequestMapping("/recommendation")
@Tag(name = "Recommendation API", description = "API для получения рекомендаций банковских продуктов")
public class RecommendationController {

    private static final Logger logger = LoggerFactory.getLogger(RecommendationController.class);

    private final RecommendationService recommendationService;

    @Autowired
    public RecommendationController(RecommendationService recommendationService) {
        this.recommendationService = recommendationService;
    }

    @GetMapping("/{userId}")
    @Operation(
            summary = "Получить рекомендации продуктов для пользователя",
            description = "Возвращает список рекомендованных банковских продуктов на основе правил"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Успешный запрос",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = RecommendationDTO.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Неверный формат ID пользователя"
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Внутренняя ошибка сервера"
            )
    })
    public ResponseEntity<RecommendationDTO> getRecommendations(
            @Parameter(
                    description = "UUID пользователя",
                    example = "cd515076-5d8a-44be-930e-8d4fcb79f42d",
                    required = true
            )
            @PathVariable String userId) {

        logger.info("Received recommendation request for user: {}", userId);

        try {
            UUID userUuid = UUID.fromString(userId);
            RecommendationDTO recommendations = recommendationService.getRecommendations(userUuid);

            logger.debug("Returning {} recommendations for user {}",
                    recommendations.getRecommendations().size(), userId);

            return ResponseEntity.ok(recommendations);

        } catch (IllegalArgumentException e) {
            logger.error("Invalid UUID format: {}", userId, e);
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            logger.error("Error processing recommendation request for user {}", userId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/{userId}/statistics")
    @Operation(
            summary = "Получить статистику по пользователю",
            description = """
                Возвращает детальную статистику транзакций пользователя для отладки.
                
                ### Формат ответа:
                - Агрегированные данные по типам продуктов (DEBIT, INVEST, SAVING, CREDIT)
                - Суммы депозитов и снятий в рублях
                - Количество транзакций
                - Детальная проверка всех бизнес-правил
                - Результат проверки каждого условия
                
                ### Использование:
                Используйте этот эндпойнт для отладки, чтобы понять, почему пользователь
                получает или не получает определенные рекомендации.
                """
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Статистика успешно получена",
                    content = @Content(mediaType = "text/plain")
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Неверный формат ID пользователя"
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Внутренняя ошибка сервера при получении статистики"
            )
    })
    public ResponseEntity<String> getUserStatistics(
            @Parameter(
                    description = "UUID пользователя",
                    example = "cd515076-5d8a-44be-930e-8d4fcb79f42d",
                    required = true
            )
            @PathVariable String userId) {

        logger.debug("Received statistics request for user: {}", userId);

        try {
            UUID userUuid = UUID.fromString(userId);
            String statistics = recommendationService.getUserStatistics(userUuid);

            return ResponseEntity.ok(statistics);

        } catch (IllegalArgumentException e) {
            logger.error("Invalid UUID format for statistics: {}", userId, e);
            return ResponseEntity.badRequest().body("Invalid UUID format");
        } catch (Exception e) {
            logger.error("Error getting statistics for user {}", userId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                 .body("Error getting statistics: " + e.getMessage());
        }
    }

    @GetMapping("/test")
    @Operation(
            summary = "Тестовый метод для проверки рекомендаций",
            description = """
                Возвращает рекомендации для предопределенных тестовых пользователей.
                
                ### Тестовые пользователи:
                1. `cd515076-5d8a-44be-930e-8d4fcb79f42d` - для проверки Invest 500
                2. `d4a4d619-9a0c-4fc5-b0cb-76c49409546b` - для проверки Top Saving
                3. `1f9b149c-6577-448a-bc94-16bea229b71a` - для проверки Простого кредита
                
                ### Возвращаемый формат:
                Текстовое представление с результатами проверки всех трех пользователей.
                Каждый блок содержит ID пользователя, количество рекомендаций и список рекомендованных продуктов.
                """
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Тест успешно выполнен",
                    content = @Content(mediaType = "text/plain")
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Внутренняя ошибка сервера при выполнении теста"
            )
    })
    public ResponseEntity<String> testRecommendations() {
        try {
            Thread.sleep(100);
            var testResults = recommendationService.getTestRecommendations();
            StringBuilder result = getStringBuilder(testResults);

            return ResponseEntity.ok(result.toString());
        } catch (Exception e) {
            logger.error("Error in test method", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                 .body("Error: " + e.getMessage());
        }
    }

    private static StringBuilder getStringBuilder(List<RecommendationDTO> testResults) {
        StringBuilder result = new StringBuilder();
        result.append("Test Recommendations Results:\n\n");

        for (var dto : testResults) {
            result.append(String.format("User: %s%n", dto.getUserId()));
            result.append(String.format("Recommendations: %d%n", dto.getRecommendations().size()));
            for (var rec : dto.getRecommendations()) {
                result.append(String.format("  - %s (ID: %s)%n", rec.getName(), rec.getId()));
            }
            result.append("\n");
        }
        return result;
    }
}