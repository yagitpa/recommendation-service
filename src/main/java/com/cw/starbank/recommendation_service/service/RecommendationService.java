package com.cw.starbank.recommendation_service.service;

import com.cw.starbank.recommendation_service.aggregator.UserTransactionAggregator;
import com.cw.starbank.recommendation_service.dto.ProductInfo;
import com.cw.starbank.recommendation_service.dto.RecommendationDTO;
import com.cw.starbank.recommendation_service.repository.RecommendationRepository;
import com.cw.starbank.recommendation_service.util.ProductConstants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Основной сервис для оркестрации процесса формирования рекомендаций.
 * <p>
 * Координирует взаимодействие между репозиторием данных и движком бизнес-правил.
 * Отвечает за получение агрегированных данных о пользователе, проверку правил
 * и преобразование результатов в формат DTO для возврата клиенту.
 * </p>
 *
 * <h3>Основные этапы работы:</h3>
 * <ol>
 *   <li>Проверка существования пользователя в системе</li>
 *   <li>Получение и агрегация транзакций пользователя</li>
 *   <li>Проверка бизнес-правил на основе агрегированных данных</li>
 *   <li>Формирование ответа в формате DTO</li>
 * </ol>
 *
 * @see RuleEngineService
 * @see RecommendationRepository
 * @see RecommendationDTO
 * @since 1.0.0
 */
@Slf4j
@Service
public class RecommendationService {

    private final RecommendationRepository repository;
    private final RuleEngineService ruleEngineService;

    @Autowired
    public RecommendationService(
            RecommendationRepository repository,
            RuleEngineService ruleEngineService) {
        this.repository = repository;
        this.ruleEngineService = ruleEngineService;
    }

    /**
     * Получает рекомендации продуктов для указанного пользователя
     */
    public RecommendationDTO getRecommendations(UUID userId) {
        if (!repository.userExists(userId)) {
            return new RecommendationDTO(userId, new ArrayList<>());
        }

        // Агрегация
        UserTransactionAggregator aggregator = repository.getUserTransactionAggregator(userId);

        // Валидация
        List<ProductInfo> recommendedProducts = ruleEngineService.checkAllRules(aggregator);

        // Конвертация
        List<RecommendationDTO.ProductRecommendation> recommendations =
                recommendedProducts.stream()
                                   .map(this::convertToRecommendation)
                                   .toList();

        return new RecommendationDTO(userId, recommendations);
    }

    /**
     * Конвертирует ProductInfo в ProductRecommendation.
     * Метод имеет package-private доступ для тестирования.
     */
    RecommendationDTO.ProductRecommendation convertToRecommendation(ProductInfo productInfo) {
        return new RecommendationDTO.ProductRecommendation(
                productInfo.getId(),
                productInfo.getName(),
                productInfo.getDescription()
        );
    }

    /**
     * Получает рекомендации для тестовых пользователей (для отладки)
     */
    public List<RecommendationDTO> getTestRecommendations() {
        List<UUID> testUserIds = List.of(
                ProductConstants.TEST_USER_INVEST,
                ProductConstants.TEST_USER_SAVING,
                ProductConstants.TEST_USER_CREDIT
        );

        return testUserIds.stream()
                          .map(this::getRecommendations)
                          .toList();
    }

    /**
    * Метод для отладки - выводит детальную информацию о транзакциях пользователя.
    */
    public String getUserStatistics(UUID userId) {
        StringBuilder result = new StringBuilder();

        result.append("=== ДЕТАЛЬНАЯ ИНФОРМАЦИЯ ПО ПОЛЬЗОВАТЕЛЮ ===\n\n");
        result.append("User ID: ").append(userId).append("\n");

        if (!repository.userExists(userId)) {
            result.append("\nПользователь не найден в базе данных!\n");
            return result.toString();
        }

        UserTransactionAggregator aggregator = repository.getUserTransactionAggregator(userId);

        result.append("\n=== АГРЕГИРОВАННЫЕ ДАННЫЕ (в рублях) ===\n\n");

        // Информация по типам продуктов
        for (String productType : List.of("DEBIT", "INVEST", "SAVING", "CREDIT")) {
            if (aggregator.usesProductType(productType)) {
                var stats = aggregator.getStats(productType);
                result.append(productType).append(":\n");
                result.append("  Депозиты:    ").append(stats.getTotalDeposits()).append(" ₽\n");
                result.append("  Снятия:      ").append(stats.getTotalWithdrawals()).append(" ₽\n");
                result.append("  Чистый поток: ").append(stats.getNetAmount()).append(" ₽\n");
                result.append("  Количество транзакций: ").append(stats.getTransactionCount()).append("\n\n");
            } else {
                result.append(productType).append(": Нет транзакций\n\n");
            }
        }

        // Проверка правил
        result.append("=== ПРОВЕРКА ПРАВИЛ ===\n\n");

        // Invest 500
        boolean invest500Rule1 = aggregator.usesProductType("DEBIT");
        boolean invest500Rule2 = !aggregator.usesProductType("INVEST");
        boolean invest500Rule3 = aggregator.getDeposits("SAVING").compareTo(new BigDecimal("1000")) > 0;
        result.append("Invest 500:\n");
        result.append("  1. Использует DEBIT: ").append(invest500Rule1).append("\n");
        result.append("  2. Не использует INVEST: ").append(invest500Rule2).append("\n");
        result.append("  3. SAVING deposits > 1000 ₽: ").append(aggregator.getDeposits("SAVING"))
              .append(" > 1000 = ").append(invest500Rule3).append("\n");
        result.append("  ИТОГО: ").append(invest500Rule1 && invest500Rule2 && invest500Rule3 ? "ПРОЙДЕНО" : "НЕ ПРОЙДЕНО").append("\n\n");

        // Top Saving
        boolean topSavingRule1 = aggregator.usesProductType("DEBIT");
        boolean topSavingRule2 = aggregator.getDeposits("DEBIT").compareTo(new BigDecimal("50000")) >= 0 ||
                aggregator.getDeposits("SAVING").compareTo(new BigDecimal("50000")) >= 0;
        boolean topSavingRule3 = aggregator.getDeposits("DEBIT").compareTo(aggregator.getWithdrawals("DEBIT")) > 0;
        result.append("Top Saving:\n");
        result.append("  1. Использует DEBIT: ").append(topSavingRule1).append("\n");
        result.append("  2. DEBIT deposits >= 50000 ₽ ИЛИ SAVING deposits >= 50000 ₽: ").append(topSavingRule2).append("\n");
        result.append("  3. DEBIT deposits > DEBIT withdrawals: ")
              .append(aggregator.getDeposits("DEBIT")).append(" > ")
              .append(aggregator.getWithdrawals("DEBIT")).append(" = ").append(topSavingRule3).append("\n");
        result.append("  ИТОГО: ").append(topSavingRule1 && topSavingRule2 && topSavingRule3 ? "ПРОЙДЕНО" : "НЕ ПРОЙДЕНО").append("\n\n");

        // Простой кредит
        boolean simpleCreditRule1 = !aggregator.usesProductType("CREDIT");
        boolean simpleCreditRule2 = aggregator.getDeposits("DEBIT").compareTo(aggregator.getWithdrawals("DEBIT")) > 0;
        boolean simpleCreditRule3 = aggregator.getWithdrawals("DEBIT").compareTo(new BigDecimal("100000")) > 0;
        result.append("Простой кредит:\n");
        result.append("  1. Не использует CREDIT: ").append(simpleCreditRule1).append("\n");
        result.append("  2. DEBIT deposits > DEBIT withdrawals: ").append(simpleCreditRule2).append("\n");
        result.append("  3. DEBIT withdrawals > 100000 ₽: ")
              .append(aggregator.getWithdrawals("DEBIT")).append(" > 100000 = ").append(simpleCreditRule3).append("\n");
        result.append("  ИТОГО: ").append(simpleCreditRule1 && simpleCreditRule2 && simpleCreditRule3 ? "ПРОЙДЕНО" : "НЕ ПРОЙДЕНО").append("\n");

        return result.toString();
    }
}