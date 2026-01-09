package com.cw.starbank.recommendation_service.config;

import com.cw.starbank.recommendation_service.dto.ProductInfo;
import com.cw.starbank.recommendation_service.repository.RecommendationRepository;
import com.cw.starbank.recommendation_service.util.ProductConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.util.UUID;

/**
 * Компонент для проверки подключения к базе данных при запуске
 */
@Component
public class DatabaseChecker {

    private static final Logger logger = LoggerFactory.getLogger(DatabaseChecker.class);

    private final RecommendationRepository repository;

    @Autowired
    public DatabaseChecker(RecommendationRepository repository) {
        this.repository = repository;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void checkDatabaseOnStartup() {
        logger.info("=== CHECKING DATABASE CONNECTION ===");

        boolean isConnected = repository.checkDatabaseConnection();

        if (isConnected) {
            logger.info("=== DATABASE CONNECTION SUCCESSFUL ===");

            checkTestUsers();
            checkProductsFromConstants();
            listAllProducts();

        } else {
            logger.error("=== DATABASE CONNECTION FAILED ===");
            logger.error("Please ensure that:");
            logger.error("1. File 'transaction.mv.db' exists in project root");
            logger.error("2. Database file is not corrupted");
            logger.error("3. No other application is using the database file");
        }
    }

    private void checkTestUsers() {
        String[] testUserIds = {
                "cd515076-5d8a-44be-930e-8d4fcb79f42d",
                "d4a4d619-9a0c-4fc5-b0cb-76c49409546b",
                "1f9b149c-6577-448a-bc94-16bea229b71a"
        };

        logger.info("=== CHECKING TEST USERS ===");
        for (String userId : testUserIds) {
            try {
                boolean exists = repository.userExists(UUID.fromString(userId));
                logger.info("  User {}: {}", userId, exists ? "FOUND" : "NOT FOUND");
            } catch (Exception e) {
                logger.error("  User {}: ERROR - {}", userId, e.getMessage());
            }
        }
    }

    private void checkProductsFromConstants() {
        logger.info("=== CHECKING PRODUCTS FROM CONSTANTS ===");

        checkProduct("Invest 500", ProductConstants.INVEST_500_ID);
        checkProduct("Top Saving", ProductConstants.TOP_SAVING_ID);
        checkProduct("Simple Credit", ProductConstants.SIMPLE_CREDIT_ID);
    }

    private void checkProduct(String productName, UUID expectedId) {
        try {
            var product = repository.getProductById(expectedId);
            if (product.isPresent()) {
                logger.info("  {}: FOUND - ID={}, Name='{}', Type={}",
                        productName,
                        expectedId,
                        product.get().getName(),
                        product.get().getType()
                );
            } else {
                logger.error("  {}: NOT FOUND - ID {} does not exist in database!",
                        productName, expectedId);
            }
        } catch (Exception e) {
            logger.error("  {}: ERROR checking product - {}", productName, e.getMessage());
        }
    }

    private void listAllProducts() {
        logger.info("=== ALL PRODUCTS IN DATABASE ===");
        try {
            var allProducts = repository.getAllProducts();
            if (allProducts.isEmpty()) {
                logger.info("  No products found in database");
            } else {
                logger.info("  Found {} products:", allProducts.size());
                for (ProductInfo product : allProducts) {
                    // Описание не выводим, так как его нет в базе
                    logger.info("    - ID: {}, Name: '{}', Type: {}",
                            product.getId(), product.getName(), product.getType());
                }
            }
        } catch (Exception e) {
            logger.error("  Error listing products: {}", e.getMessage());
        }
    }
}