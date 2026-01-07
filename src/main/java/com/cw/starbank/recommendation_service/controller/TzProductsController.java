package com.cw.starbank.recommendation_service.controller;

import com.cw.starbank.recommendation_service.dto.ProductInfo;
import com.cw.starbank.recommendation_service.util.ProductConstants;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

/**
 * Контроллер для проверки продуктов из технического задания.
 * <p>
 * Предоставляет эндпойнты для получения информации о продуктах,
 * определенных в техническом задании. Используется для отладки
 * и проверки корректности настроек продуктов.
 * </p>
 *
 * <h3>Доступные эндпойнты:</h3>
 * <ul>
 *   <li>{@code GET /api/tz-products} - получение списка продуктов из ТЗ</li>
 *   <li>{@code GET /api/tz-products/check} - детальная проверка продуктов из ТЗ</li>
 * </ul>
 *
 * @since 1.0.0
 */
@RestController
@RequestMapping("/tz-products")
@Tag(name = "Technical Specification Products", description = "API для работы с продуктами из технического задания")
public class TzProductsController {

    /**
     * Получает список продуктов из технического задания.
     * <p>
     * Возвращает три предопределенных продукта, которые используются
     * для рекомендаций в соответствии с техническим заданием.
     * </p>
     *
     * @return список продуктов из ТЗ
     */
    @GetMapping
    @Operation(
            summary = "Получить продукты из технического задания",
            description = "Возвращает список из трех продуктов, определенных в техническом задании: " +
                    "Invest 500, Top Saving и Простой кредит"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Успешный запрос",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ProductInfo.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Внутренняя ошибка сервера"
            )
    })
    public List<ProductInfo> getTzProducts() {
        List<ProductInfo> products = new ArrayList<>();

        // Invest 500 из ТЗ
        products.add(new ProductInfo(
                ProductConstants.INVEST_500_ID,
                ProductConstants.INVEST_500_NAME,
                ProductConstants.INVEST_500_TYPE,
                ProductConstants.INVEST_500_DESCRIPTION
        ));

        // Top Saving из ТЗ
        products.add(new ProductInfo(
                ProductConstants.TOP_SAVING_ID,
                ProductConstants.TOP_SAVING_NAME,
                ProductConstants.TOP_SAVING_TYPE,
                ProductConstants.TOP_SAVING_DESCRIPTION
        ));

        // Простой кредит из ТЗ
        products.add(new ProductInfo(
                ProductConstants.SIMPLE_CREDIT_ID,
                ProductConstants.SIMPLE_CREDIT_NAME,
                ProductConstants.SIMPLE_CREDIT_TYPE,
                ProductConstants.SIMPLE_CREDIT_DESCRIPTION
        ));

        return products;
    }

    /**
     * Проверяет корректность настроек продуктов из технического задания.
     * <p>
     * Возвращает текстовое описание с детальной информацией о каждом продукте:
     * идентификатор, название, тип и длину описания. Используется для отладки.
     * </p>
     *
     * @return строковое представление информации о продуктах
     */
    @GetMapping("/check")
    @Operation(
            summary = "Проверить продукты из ТЗ",
            description = "Возвращает детальную информацию о продуктах из технического задания " +
                    "в текстовом формате для отладки"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Успешный запрос",
                    content = @Content(mediaType = "text/plain")
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Внутренняя ошибка сервера"
            )
    })
    public String checkTzProducts() {
        StringBuilder result = new StringBuilder();
        result.append("=== PRODUCTS FROM TECHNICAL SPECIFICATION ===\n\n");

        result.append("1. Invest 500:\n");
        result.append("   ID: ").append(ProductConstants.INVEST_500_ID).append("\n");
        result.append("   Name: ").append(ProductConstants.INVEST_500_NAME).append("\n");
        result.append("   Type: ").append(ProductConstants.INVEST_500_TYPE).append("\n");
        result.append("   Description length: ").append(ProductConstants.INVEST_500_DESCRIPTION.length()).append(" chars\n\n");

        result.append("2. Top Saving:\n");
        result.append("   ID: ").append(ProductConstants.TOP_SAVING_ID).append("\n");
        result.append("   Name: ").append(ProductConstants.TOP_SAVING_NAME).append("\n");
        result.append("   Type: ").append(ProductConstants.TOP_SAVING_TYPE).append("\n");
        result.append("   Description length: ").append(ProductConstants.TOP_SAVING_DESCRIPTION.length()).append(" chars\n\n");

        result.append("3. Простой кредит:\n");
        result.append("   ID: ").append(ProductConstants.SIMPLE_CREDIT_ID).append("\n");
        result.append("   Name: ").append(ProductConstants.SIMPLE_CREDIT_NAME).append("\n");
        result.append("   Type: ").append(ProductConstants.SIMPLE_CREDIT_TYPE).append("\n");
        result.append("   Description length: ").append(ProductConstants.SIMPLE_CREDIT_DESCRIPTION.length()).append(" chars\n");

        return result.toString();
    }
}