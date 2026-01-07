package com.cw.starbank.recommendation_service.aggregator;

import lombok.Data;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

/**
 * Агрегирует транзакции пользователя по типам продуктов.
 * Хранит суммы DEPOSIT и WITHDRAW для каждого типа продукта.
 * <p>
 * <strong>Внимание:</strong> Типы транзакций должны быть 'DEPOSIT' и 'WITHDRAW'
 * (в соответствии с полем TYPE в таблице TRANSACTIONS).
 * </p>
 */
@Data
public class UserTransactionAggregator {

    private final Map<String, TransactionStats> statsByProductType = new HashMap<>();

    @Data
    public static class TransactionStats {
        private BigDecimal totalDeposits = BigDecimal.ZERO;
        private BigDecimal totalWithdrawals = BigDecimal.ZERO;
        private int transactionCount = 0;

        public boolean hasTransactions() {
            return transactionCount > 0;
        }

        public BigDecimal getNetAmount() {
            return totalDeposits.subtract(totalWithdrawals);
        }
    }

    /**
     * Добавляет транзакцию в агрегатор.
     *
     * @param productType     тип продукта (DEBIT, INVEST, SAVING, CREDIT)
     * @param transactionType тип транзакции (должен быть 'DEPOSIT' или 'WITHDRAW')
     * @param amount          сумма транзакции в рублях
     */
    public void addTransaction(String productType, String transactionType, BigDecimal amount) {
        TransactionStats stats = statsByProductType.computeIfAbsent(productType, k -> new TransactionStats());

        // ИСПРАВЛЕНО: Используем 'WITHDRAW' вместо 'WITHDRAWAL'
        if ("DEPOSIT".equals(transactionType)) {
            stats.setTotalDeposits(stats.getTotalDeposits().add(amount));
        } else if ("WITHDRAW".equals(transactionType)) { // ИСПРАВЛЕНО: WITHDRAW
            stats.setTotalWithdrawals(stats.getTotalWithdrawals().add(amount));
        } else {
            // Логируем неожиданный тип транзакции (если включено логирование)
            System.err.println("WARNING: Unknown transaction type: " + transactionType);
        }

        stats.setTransactionCount(stats.getTransactionCount() + 1);
    }

    /**
     * Проверяет, использует ли пользователь продукт определенного типа
     */
    public boolean usesProductType(String productType) {
        TransactionStats stats = statsByProductType.get(productType);
        return stats != null && stats.hasTransactions();
    }

    /**
     * Получает статистику по типу продукта
     */
    public TransactionStats getStats(String productType) {
        return statsByProductType.getOrDefault(productType, new TransactionStats());
    }

    /**
     * Получает сумму пополнений по типу продукта
     */
    public BigDecimal getDeposits(String productType) {
        return getStats(productType).getTotalDeposits();
    }

    /**
     * Получает сумму трат по типу продукта
     */
    public BigDecimal getWithdrawals(String productType) {
        return getStats(productType).getTotalWithdrawals();
    }
}