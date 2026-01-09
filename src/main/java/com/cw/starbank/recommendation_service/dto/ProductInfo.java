package com.cw.starbank.recommendation_service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

/**
 * DTO для хранения информации о банковском продукте из базы данных.
 * <p>
 * Используется для передачи данных между репозиторием и бизнес-правилами.
 * Содержит основные атрибуты продукта: идентификатор, название, тип и описание.
 * </p>
 *
 * @since 1.0.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Информация о банковском продукте")
public class ProductInfo {

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
     * Название продукта.
     */
    @Schema(
            description = "Название банковского продукта",
            example = "Invest 500",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String name;

    /**
     * Тип продукта (DEBIT, INVEST, SAVING, CREDIT).
     */
    @Schema(
            description = "Тип банковского продукта",
            example = "INVEST",
            allowableValues = {"DEBIT", "INVEST", "SAVING", "CREDIT"},
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String type;

    /**
     * Описание продукта.
     */
    @Schema(
            description = "Подробное описание продукта",
            example = "Инвестиционный продукт для начинающих инвесторов",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED
    )
    private String description;
}