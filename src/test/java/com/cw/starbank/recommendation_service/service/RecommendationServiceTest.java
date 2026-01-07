package com.cw.starbank.recommendation_service.service;

import com.cw.starbank.recommendation_service.aggregator.UserTransactionAggregator;
import com.cw.starbank.recommendation_service.dto.ProductInfo;
import com.cw.starbank.recommendation_service.dto.RecommendationDTO;
import com.cw.starbank.recommendation_service.repository.RecommendationRepository;
import com.cw.starbank.recommendation_service.util.ProductConstants;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Тесты для основного сервиса рекомендаций
 */
@ExtendWith(MockitoExtension.class)
class RecommendationServiceTest {

    @Mock
    private RecommendationRepository repository;

    @Mock
    private RuleEngineService ruleEngineService;

    private RecommendationService recommendationService;

    private final UUID testUserId = UUID.fromString("cd515076-5d8a-44be-930e-8d4fcb79f42d");

    @BeforeEach
    void setUp() {
        recommendationService = new RecommendationService(repository, ruleEngineService);
    }

    @Test
    void testGetRecommendations_UserExists() {
        // Подготовка
        when(repository.userExists(testUserId)).thenReturn(true);

        UserTransactionAggregator aggregator = new UserTransactionAggregator();
        when(repository.getUserTransactionAggregator(testUserId)).thenReturn(aggregator);

        List<ProductInfo> mockRecommendations = List.of(
                new ProductInfo(
                        ProductConstants.INVEST_500_ID,
                        "Invest 500",
                        "INVEST",
                        "Описание Invest 500"
                )
        );
        when(ruleEngineService.checkAllRules(aggregator)).thenReturn(mockRecommendations);

        // Выполнение
        RecommendationDTO result = recommendationService.getRecommendations(testUserId);

        // Проверка
        assertNotNull(result);
        assertEquals(testUserId, result.getUserId());
        assertEquals(1, result.getRecommendations().size());
        assertEquals("Invest 500", result.getRecommendations().get(0).getName());

        verify(repository).userExists(testUserId);
        verify(repository).getUserTransactionAggregator(testUserId);
        verify(ruleEngineService).checkAllRules(aggregator);
    }

    @Test
    void testGetRecommendations_UserNotExists() {
        // Подготовка
        when(repository.userExists(testUserId)).thenReturn(false);

        // Выполнение
        RecommendationDTO result = recommendationService.getRecommendations(testUserId);

        // Проверка
        assertNotNull(result);
        assertEquals(testUserId, result.getUserId());
        assertTrue(result.getRecommendations().isEmpty());

        verify(repository).userExists(testUserId);
        verify(repository, never()).getUserTransactionAggregator(any());
        verify(ruleEngineService, never()).checkAllRules(any());
    }

    @Test
    void testGetUserStatistics_UserExists() {
        // Подготовка
        when(repository.userExists(testUserId)).thenReturn(true);

        UserTransactionAggregator aggregator = new UserTransactionAggregator();
        aggregator.addTransaction("DEBIT", "DEPOSIT", new BigDecimal("1000"));
        when(repository.getUserTransactionAggregator(testUserId)).thenReturn(aggregator);

        List<ProductInfo> mockRecommendations = List.of(
                new ProductInfo(
                        ProductConstants.INVEST_500_ID,
                        "Invest 500",
                        "INVEST",
                        "Описание"
                )
        );
        when(ruleEngineService.checkAllRules(aggregator)).thenReturn(mockRecommendations);

        // Выполнение
        String statistics = recommendationService.getUserStatistics(testUserId);

        // Проверка
        assertNotNull(statistics);
        assertTrue(statistics.contains("User Statistics for:"));
        assertTrue(statistics.contains("DEBIT Products:"));
        assertTrue(statistics.contains("Invest 500: true"));
        assertTrue(statistics.contains("Top Saving: false"));
        assertTrue(statistics.contains("Simple Credit: false"));

        verify(repository).userExists(testUserId);
        verify(repository).getUserTransactionAggregator(testUserId);
        verify(ruleEngineService, times(1)).checkAllRules(aggregator); // Только 1 раз!
    }

    @Test
    void testGetUserStatistics_UserNotExists() {
        // Подготовка
        when(repository.userExists(testUserId)).thenReturn(false);

        // Выполнение
        String statistics = recommendationService.getUserStatistics(testUserId);

        // Проверка
        assertNotNull(statistics);
        assertTrue(statistics.contains("User not found:"));

        verify(repository).userExists(testUserId);
        verify(repository, never()).getUserTransactionAggregator(any());
        verify(ruleEngineService, never()).checkAllRules(any());
    }

    @Test
    void testConvertToRecommendation() {
        // Подготовка
        ProductInfo productInfo = new ProductInfo(
                ProductConstants.INVEST_500_ID,
                "Invest 500",
                "INVEST",
                "Test description"
        );

        // Выполнение - используем reflection для вызова приватного метода
        var recommendation = recommendationService.convertToRecommendation(productInfo);

        // Проверка
        assertEquals(ProductConstants.INVEST_500_ID, recommendation.getId());
        assertEquals("Invest 500", recommendation.getName());
        assertEquals("Test description", recommendation.getText());
    }
}