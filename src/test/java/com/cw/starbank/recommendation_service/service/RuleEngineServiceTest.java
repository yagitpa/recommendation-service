package com.cw.starbank.recommendation_service.service;

import com.cw.starbank.recommendation_service.aggregator.UserTransactionAggregator;
import com.cw.starbank.recommendation_service.dto.ProductInfo;
import com.cw.starbank.recommendation_service.util.ProductConstants;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Тесты для сервиса проверки правил рекомендаций
 */
@ExtendWith(MockitoExtension.class)
class RuleEngineServiceTest {

    private RuleEngineService ruleEngineService;
    private UserTransactionAggregator aggregator;

    @BeforeEach
    void setUp() {
        ruleEngineService = new RuleEngineService();
        aggregator = new UserTransactionAggregator();
    }

    @Test
    void testInvest500Rule_AllConditionsMet() {
        // Подготовка: пользователь соответствует всем условиям для Invest 500
        // 1. Использует DEBIT
        aggregator.addTransaction(ProductConstants.PRODUCT_TYPE_DEBIT, "DEPOSIT", new BigDecimal("500"));
        // 2. Не использует INVEST (не добавляем транзакций по INVEST)
        // 3. Сумма пополнений SAVING > 1000
        aggregator.addTransaction(ProductConstants.PRODUCT_TYPE_SAVING, "DEPOSIT", new BigDecimal("1500"));

        // Выполнение
        List<ProductInfo> recommendations = ruleEngineService.checkAllRules(aggregator);

        // Проверка
        assertEquals(1, recommendations.size());
        ProductInfo product = recommendations.get(0);
        assertEquals(ProductConstants.INVEST_500_ID, product.getId());
        assertEquals("Invest 500", product.getName());
    }

    @Test
    void testInvest500Rule_UsesInvest_ShouldFail() {
        // Подготовка: пользователь использует INVEST продукты
        aggregator.addTransaction(ProductConstants.PRODUCT_TYPE_DEBIT, "DEPOSIT", new BigDecimal("500"));
        aggregator.addTransaction(ProductConstants.PRODUCT_TYPE_INVEST, "DEPOSIT", new BigDecimal("100")); // Правило 2 нарушено
        aggregator.addTransaction(ProductConstants.PRODUCT_TYPE_SAVING, "DEPOSIT", new BigDecimal("1500"));

        // Выполнение
        List<ProductInfo> recommendations = ruleEngineService.checkAllRules(aggregator);

        // Проверка: рекомендаций быть не должно
        assertTrue(recommendations.isEmpty());
    }

    @Test
    void testInvest500Rule_NoDebit_ShouldFail() {
        // Подготовка: пользователь не использует DEBIT продукты
        // Не добавляем DEBIT транзакций
        aggregator.addTransaction(ProductConstants.PRODUCT_TYPE_SAVING, "DEPOSIT", new BigDecimal("1500"));

        // Выполнение
        List<ProductInfo> recommendations = ruleEngineService.checkAllRules(aggregator);

        // Проверка
        assertTrue(recommendations.isEmpty());
    }

    @Test
    void testTopSavingRule_AllConditionsMet_DebitLargeDeposits() {
        // Подготовка: пользователь соответствует всем условиям для Top Saving через DEBIT
        // 1. Использует DEBIT
        aggregator.addTransaction(ProductConstants.PRODUCT_TYPE_DEBIT, "DEPOSIT", new BigDecimal("60000")); // > 50000
        // 2. Deposits > Withdrawals для DEBIT
        aggregator.addTransaction(ProductConstants.PRODUCT_TYPE_DEBIT, "WITHDRAWAL", new BigDecimal("10000"));

        // Выполнение
        List<ProductInfo> recommendations = ruleEngineService.checkAllRules(aggregator);

        // Проверка
        assertEquals(1, recommendations.size());
        ProductInfo product = recommendations.get(0);
        assertEquals(ProductConstants.TOP_SAVING_ID, product.getId());
        assertEquals("Top Saving", product.getName());
    }

    @Test
    void testTopSavingRule_DebitWithdrawalsExceedDeposits_ShouldFail() {
        // Подготовка: траты DEBIT превышают пополнения
        aggregator.addTransaction(ProductConstants.PRODUCT_TYPE_DEBIT, "DEPOSIT", new BigDecimal("10000"));
        aggregator.addTransaction(ProductConstants.PRODUCT_TYPE_DEBIT, "WITHDRAWAL", new BigDecimal("20000")); // > deposits

        // Выполнение
        List<ProductInfo> recommendations = ruleEngineService.checkAllRules(aggregator);

        // Проверка
        assertTrue(recommendations.isEmpty());
    }

    @Test
    void testMultipleRulesCanPass() {
        // Подготовка: пользователь соответствует нескольким правилам
        // Условия для Invest 500:
        aggregator.addTransaction(ProductConstants.PRODUCT_TYPE_DEBIT, "DEPOSIT", new BigDecimal("1000"));
        aggregator.addTransaction(ProductConstants.PRODUCT_TYPE_SAVING, "DEPOSIT", new BigDecimal("1500"));

        // Условия для Top Saving:
        aggregator.addTransaction(ProductConstants.PRODUCT_TYPE_SAVING, "DEPOSIT", new BigDecimal("60000")); // > 50000

        // Условия для Простого кредита НЕ выполняются (нет больших трат)

        // Выполнение
        List<ProductInfo> recommendations = ruleEngineService.checkAllRules(aggregator);

        // Проверка: должны получить Invest 500 и Top Saving
        assertEquals(2, recommendations.size());
        assertTrue(recommendations.stream().anyMatch(p -> p.getId().equals(ProductConstants.INVEST_500_ID)));
        assertTrue(recommendations.stream().anyMatch(p -> p.getId().equals(ProductConstants.TOP_SAVING_ID)));
    }
}