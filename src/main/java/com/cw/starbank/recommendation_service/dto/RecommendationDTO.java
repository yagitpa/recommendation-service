package com.cw.starbank.recommendation_service.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

/**
 * Основной DTO для ответа с рекомендациями продуктов.
 * <p>
 * Соответствует формату, указанному в техническом задании.
 * Содержит идентификатор пользователя и список рекомендованных продуктов.
 * </p>
 *
 * <h3>Пример JSON ответа:</h3>
 * <pre>
 * {
 *   "user_id": "cd515076-5d8a-44be-930e-8d4fcb79f42d",
 *   "recommendations": [
 *     {
 *       "name": "Invest 500",
 *       "id": "147f6a0f-3b91-413b-ab99-87f081d60d5a",
 *       "text": "Описание продукта Invest 500..."
 *     }
 *   ]
 * }
 * </pre>
 *
 * @since 1.0.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Основной ответ с рекомендациями продуктов")
public class RecommendationDTO {

    /**
     * Идентификатор пользователя, для которого сформированы рекомендации.
     */
    @JsonProperty("user_id")
    @Schema(
            description = "Уникальный идентификатор пользователя",
            example = "cd515076-5d8a-44be-930e-8d4fcb79f42d",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private UUID userId;

    /**
     * Список рекомендованных продуктов.
     * Может быть пустым, если нет подходящих рекомендаций.
     */
    @Schema(
            description = "Список рекомендованных продуктов",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private List<ProductRecommendation> recommendations;

    /**
     * DTO для информации об отдельном рекомендованном продукте.
     * <p>
     * Содержит основные атрибуты продукта: название, идентификатор и описание.
     * Соответствует формату, ожидаемому клиентом API.
     * </p>
     *
     * @since 1.0.0
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "Информация о рекомендованном продукте")
    public static class ProductRecommendation {

        /**
         * Название продукта.
         */
        @Schema(
                description = "Название банковского продукта",
                example = "Invest 500",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        private String name;

        /**
         * Уникальный идентификатор продукта.
         */
        @Schema(
                description = "Уникальный идентификатор продукта",
                example = "147f6a0f-3b91-413b-ab99-87f081d60d5a",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        private UUID id;

        /**
         * Описание продукта.
         */
        @Schema(
                description = "Подробное описание продукта",
                example = "Инвестиционный продукт для начинающих с минимальным взносом 500 рублей",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        private String text;
    }
}