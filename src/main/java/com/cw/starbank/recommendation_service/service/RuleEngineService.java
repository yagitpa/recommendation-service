package com.cw.starbank.recommendation_service.service;

import com.cw.starbank.recommendation_service.aggregator.UserTransactionAggregator;
import com.cw.starbank.recommendation_service.dto.ProductInfo;
import com.cw.starbank.recommendation_service.util.ProductConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Сервис для проверки бизнес-правил рекомендаций банковских продуктов.
 * <p>
 * Реализует проверку всех правил, описанных в техническом задании, на основе
 * агрегированных данных о транзакциях пользователя. Правила проверяются независимо,
 * пользователь может получить несколько рекомендаций одновременно.
 * </p>
 *
 * <h3>Бизнес-правила:</h3>
 * <ul>
 *   <li><strong>Invest 500</strong>: для начинающих инвесторов с накоплениями</li>
 *   <li><strong>Top Saving</strong>: для клиентов с крупными накоплениями</li>
 *   <li><strong>Простой кредит</strong>: для надежных заемщиков без текущих кредитов</li>
 * </ul>
 *
 * <h3>Особенности реализации:</h3>
 * <ul>
 *   <li>Все суммы уже конвертированы из копеек в рубли на уровне репозитория</li>
 *   <li>Используется BigDecimal для точных финансовых расчетов</li>
 *   <li>Правила проверяются в порядке приоритета продуктов</li>
 *   <li>Поддерживается множественная рекомендация</li>
 * </ul>
 *
 * @see UserTransactionAggregator
 * @see ProductConstants
 * @since 1.0.0
 */
@Service
public class RuleEngineService {

    private static final Logger logger = LoggerFactory.getLogger(RuleEngineService.class);

    // Пороговые значения в рублях
    private static final BigDecimal THRESHOLD_1000 = new BigDecimal("1000");
    private static final BigDecimal THRESHOLD_50000 = new BigDecimal("50000");
    private static final BigDecimal THRESHOLD_100000 = new BigDecimal("100000");

    /**
     * Проверяет все бизнес-правила на основе агрегированных данных пользователя.
     * <p>
     * Метод последовательно проверяет каждое правило из технического задания
     * и возвращает список продуктов, для которых выполнены все условия.
     * </p>
     *
     * @param aggregator агрегатор транзакционных данных пользователя
     * @return список рекомендованных продуктов (может быть пустым)
     */
    public List<ProductInfo> checkAllRules(UserTransactionAggregator aggregator) {
        logger.debug("Начало проверки правил рекомендаций");

        List<ProductInfo> recommendations = new ArrayList<>();

        // Проверяем Invest 500
        if (checkInvest500Rule(aggregator)) {
            logger.info("Правило Invest 500 выполнено");
            recommendations.add(createInvest500Product());
        }

        // Проверяем Top Saving
        if (checkTopSavingRule(aggregator)) {
            logger.info("Правило Top Saving выполнено");
            recommendations.add(createTopSavingProduct());
        }

        // Проверяем Простой кредит
        if (checkSimpleCreditRule(aggregator)) {
            logger.info("Правило Простой кредит выполнено");
            recommendations.add(createSimpleCreditProduct());
        }

        logger.info("Проверка правил завершена. Найдено рекомендаций: {}", recommendations.size());
        return recommendations;
    }

    /**
     * Проверяет правило для продукта "Invest 500".
     * <p>
     * Условия правила:
     * <ol>
     *   <li>Пользователь использует хотя бы один продукт типа DEBIT</li>
     *   <li>Пользователь НЕ использует продукты типа INVEST</li>
     *   <li>Сумма пополнений продуктов типа SAVING > 1,000 ₽</li>
     * </ol>
     *
     * @param aggregator агрегатор данных пользователя
     * @return true если все условия выполнены
     */
    private boolean checkInvest500Rule(UserTransactionAggregator aggregator) {
        logger.debug("Проверка правила Invest 500");

        // 1. Использует DEBIT продукты
        boolean usesDebit = aggregator.usesProductType(ProductConstants.PRODUCT_TYPE_DEBIT);
        if (!usesDebit) {
            logger.debug("Правило Invest 500 не выполнено: пользователь не использует DEBIT продукты");
            return false;
        }

        // 2. Не использует INVEST продукты
        boolean usesInvest = aggregator.usesProductType(ProductConstants.PRODUCT_TYPE_INVEST);
        if (usesInvest) {
            logger.debug("Правило Invest 500 не выполнено: пользователь использует INVEST продукты");
            return false;
        }

        // 3. SAVING deposits > 1000 рублей
        BigDecimal savingDeposits = aggregator.getDeposits(ProductConstants.PRODUCT_TYPE_SAVING);
        boolean savingCondition = savingDeposits.compareTo(THRESHOLD_1000) > 0;

        if (!savingCondition) {
            logger.debug("Правило Invest 500 не выполнено: SAVING deposits ({}) <= {}",
                    savingDeposits, THRESHOLD_1000);
            return false;
        }

        logger.debug("Правило Invest 500 выполнено");
        return true;
    }

    /**
     * Проверяет правило для продукта "Top Saving".
     * <p>
     * Условия правила:
     * <ol>
     *   <li>Пользователь использует хотя бы один продукт типа DEBIT</li>
     *   <li>DEBIT deposits >= 50,000 ₽ ИЛИ SAVING deposits >= 50,000 ₽</li>
     *   <li>DEBIT deposits > DEBIT withdrawals</li>
     * </ol>
     *
     * @param aggregator агрегатор данных пользователя
     * @return true если все условия выполнены
     */
    private boolean checkTopSavingRule(UserTransactionAggregator aggregator) {
        logger.debug("Проверка правила Top Saving");

        // 1. Использует DEBIT продукты
        if (!aggregator.usesProductType(ProductConstants.PRODUCT_TYPE_DEBIT)) {
            logger.debug("Правило Top Saving не выполнено: пользователь не использует DEBIT продукты");
            return false;
        }

        // Получаем суммы для расчетов
        BigDecimal debitDeposits = aggregator.getDeposits(ProductConstants.PRODUCT_TYPE_DEBIT);
        BigDecimal debitWithdrawals = aggregator.getWithdrawals(ProductConstants.PRODUCT_TYPE_DEBIT);
        BigDecimal savingDeposits = aggregator.getDeposits(ProductConstants.PRODUCT_TYPE_SAVING);

        // 2. DEBIT deposits >= 50000 ИЛИ SAVING deposits >= 50000
        boolean debitCondition = debitDeposits.compareTo(THRESHOLD_50000) >= 0;
        boolean savingCondition = savingDeposits.compareTo(THRESHOLD_50000) >= 0;

        if (!debitCondition && !savingCondition) {
            logger.debug("Правило Top Saving не выполнено: недостаточно депозитов");
            return false;
        }

        // 3. DEBIT deposits > DEBIT withdrawals
        if (debitDeposits.compareTo(debitWithdrawals) <= 0) {
            logger.debug("Правило Top Saving не выполнено: снятия превышают депозиты по DEBIT");
            return false;
        }

        logger.debug("Правило Top Saving выполнено");
        return true;
    }

    /**
     * Проверяет правило для продукта "Простой кредит".
     * <p>
     * Условия правила:
     * <ol>
     *   <li>Пользователь НЕ использует продукты типа CREDIT</li>
     *   <li>DEBIT deposits > DEBIT withdrawals</li>
     *   <li>DEBIT withdrawals > 100,000 ₽</li>
     * </ol>
     *
     * @param aggregator агрегатор данных пользователя
     * @return true если все условия выполнены
     */
    private boolean checkSimpleCreditRule(UserTransactionAggregator aggregator) {
        logger.debug("Проверка правила Простой кредит");

        // 1. Не использует CREDIT продукты
        if (aggregator.usesProductType(ProductConstants.PRODUCT_TYPE_CREDIT)) {
            logger.debug("Правило Простой кредит не выполнено: пользователь использует CREDIT продукты");
            return false;
        }

        // Получаем суммы для расчетов
        BigDecimal debitDeposits = aggregator.getDeposits(ProductConstants.PRODUCT_TYPE_DEBIT);
        BigDecimal debitWithdrawals = aggregator.getWithdrawals(ProductConstants.PRODUCT_TYPE_DEBIT);

        // 2. DEBIT deposits > DEBIT withdrawals
        if (debitDeposits.compareTo(debitWithdrawals) <= 0) {
            logger.debug("Правило Простой кредит не выполнено: снятия превышают депозиты");
            return false;
        }

        // 3. DEBIT withdrawals > 100000 рублей
        if (debitWithdrawals.compareTo(THRESHOLD_100000) <= 0) {
            logger.debug("Правило Простой кредит не выполнено: недостаточно снятий ({})", debitWithdrawals);
            return false;
        }

        logger.debug("Правило Простой кредит выполнено");
        return true;
    }

    /**
     * Создает объект продукта "Invest 500".
     */
    private ProductInfo createInvest500Product() {
        return new ProductInfo(
                ProductConstants.INVEST_500_ID,
                ProductConstants.INVEST_500_NAME,
                ProductConstants.INVEST_500_TYPE,
                ProductConstants.INVEST_500_DESCRIPTION
        );
    }

    /**
     * Создает объект продукта "Top Saving".
     */
    private ProductInfo createTopSavingProduct() {
        return new ProductInfo(
                ProductConstants.TOP_SAVING_ID,
                ProductConstants.TOP_SAVING_NAME,
                ProductConstants.TOP_SAVING_TYPE,
                ProductConstants.TOP_SAVING_DESCRIPTION
        );
    }

    /**
     * Создает объект продукта "Простой кредит".
     */
    private ProductInfo createSimpleCreditProduct() {
        return new ProductInfo(
                ProductConstants.SIMPLE_CREDIT_ID,
                ProductConstants.SIMPLE_CREDIT_NAME,
                ProductConstants.SIMPLE_CREDIT_TYPE,
                ProductConstants.SIMPLE_CREDIT_DESCRIPTION
        );
    }
}