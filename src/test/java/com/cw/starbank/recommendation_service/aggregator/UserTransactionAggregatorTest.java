package com.cw.starbank.recommendation_service.aggregator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Тесты для агрегатора транзакций пользователя
 */
class UserTransactionAggregatorTest {

    private UserTransactionAggregator aggregator;

    @BeforeEach
    void setUp() {
        aggregator = new UserTransactionAggregator();
    }

    @Test
    void testAddTransaction_Deposit() {
        // Подготовка и выполнение
        aggregator.addTransaction("DEBIT", "DEPOSIT", new BigDecimal("1000"));
        aggregator.addTransaction("DEBIT", "DEPOSIT", new BigDecimal("500"));

        // Проверка
        assertTrue(aggregator.usesProductType("DEBIT"));
        assertEquals(new BigDecimal("1500"), aggregator.getDeposits("DEBIT"));
        assertEquals(BigDecimal.ZERO, aggregator.getWithdrawals("DEBIT"));
        assertEquals(2, aggregator.getStats("DEBIT").getTransactionCount());
    }

    @Test
    void testAddTransaction_Withdraw() { // ИСПРАВЛЕНО: WITHDRAW вместо WITHDRAWAL
        // Подготовка и выполнение
        aggregator.addTransaction("SAVING", "WITHDRAW", new BigDecimal("300")); // ИСПРАВЛЕНО: WITHDRAW
        aggregator.addTransaction("SAVING", "WITHDRAW", new BigDecimal("200")); // ИСПРАВЛЕНО: WITHDRAW

        // Проверка
        assertTrue(aggregator.usesProductType("SAVING"));
        assertEquals(BigDecimal.ZERO, aggregator.getDeposits("SAVING"));
        assertEquals(new BigDecimal("500"), aggregator.getWithdrawals("SAVING"));
        assertEquals(2, aggregator.getStats("SAVING").getTransactionCount());
    }

    @Test
    void testAddTransaction_MixedTypes() {
        // Подготовка и выполнение
        aggregator.addTransaction("DEBIT", "DEPOSIT", new BigDecimal("1000"));
        aggregator.addTransaction("DEBIT", "WITHDRAW", new BigDecimal("300")); // ИСПРАВЛЕНО: WITHDRAW
        aggregator.addTransaction("INVEST", "DEPOSIT", new BigDecimal("500"));

        // Проверка DEBIT
        assertTrue(aggregator.usesProductType("DEBIT"));
        assertEquals(new BigDecimal("1000"), aggregator.getDeposits("DEBIT"));
        assertEquals(new BigDecimal("300"), aggregator.getWithdrawals("DEBIT"));
        assertEquals(new BigDecimal("700"), aggregator.getStats("DEBIT").getNetAmount());
        assertEquals(2, aggregator.getStats("DEBIT").getTransactionCount());

        // Проверка INVEST
        assertTrue(aggregator.usesProductType("INVEST"));
        assertEquals(new BigDecimal("500"), aggregator.getDeposits("INVEST"));
        assertEquals(BigDecimal.ZERO, aggregator.getWithdrawals("INVEST"));
        assertEquals(1, aggregator.getStats("INVEST").getTransactionCount());

        // Проверка несуществующего типа
        assertFalse(aggregator.usesProductType("CREDIT"));
        assertEquals(BigDecimal.ZERO, aggregator.getDeposits("CREDIT"));
        assertEquals(BigDecimal.ZERO, aggregator.getWithdrawals("CREDIT"));
    }

    @Test
    void testUsesProductType_NoTransactions() {
        // Проверка
        assertFalse(aggregator.usesProductType("DEBIT"));
        assertFalse(aggregator.usesProductType("SAVING"));
        assertFalse(aggregator.usesProductType("INVEST"));
        assertFalse(aggregator.usesProductType("CREDIT"));
    }

    @Test
    void testGetStats_Empty() {
        // Выполнение
        var stats = aggregator.getStats("DEBIT");

        // Проверка
        assertNotNull(stats);
        assertEquals(BigDecimal.ZERO, stats.getTotalDeposits());
        assertEquals(BigDecimal.ZERO, stats.getTotalWithdrawals());
        assertEquals(0, stats.getTransactionCount());
        assertFalse(stats.hasTransactions());
        assertEquals(BigDecimal.ZERO, stats.getNetAmount());
    }

    @Test
    void testTransactionStats_HasTransactions() {
        // Подготовка
        aggregator.addTransaction("DEBIT", "DEPOSIT", new BigDecimal("100"));

        // Выполнение и проверка
        assertTrue(aggregator.getStats("DEBIT").hasTransactions());
        assertFalse(aggregator.getStats("SAVING").hasTransactions());
    }

    @Test
    void testAddTransaction_UnknownType() {
        // Подготовка и выполнение - добавление с неизвестным типом
        aggregator.addTransaction("DEBIT", "UNKNOWN_TYPE", new BigDecimal("100"));

        // Проверка: транзакция добавлена, но суммы не увеличились
        assertEquals(1, aggregator.getStats("DEBIT").getTransactionCount());
        assertEquals(BigDecimal.ZERO, aggregator.getDeposits("DEBIT"));
        assertEquals(BigDecimal.ZERO, aggregator.getWithdrawals("DEBIT"));
    }
}