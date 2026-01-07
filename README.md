# Сервис рекомендаций банковских продуктов

## Описание проекта
Сервис рекомендаций банковских продуктов для клиентов банка "Стар". 
Приложение анализирует транзакционную историю пользователей и рекомендует 
подходящие банковские продукты на основе бизнес-правил.

## Технологии
- Java 17
- Spring Boot 3.5.9
- H2 Database (read-only)
- JdbcTemplate
- Swagger/OpenAPI 3.0
- Lombok
- JUnit 5, Mockito

## Архитектура приложения

### Слои приложения

REST Controller Layer
• RecommendationController

Service Layer
• RecommendationService

Business Rules Layer
• RecommendationRule (интерфейс)
• Invest500Rule, TopSavingRule, etc.

Data Access Layer
• RecommendationRepository

Database Layer
• H2 Database (read-only)

### Основные компоненты

#### 1. RecommendationController
- REST контроллер с эндпойнтом `/recommendation/{userId}`
- Валидация входных параметров
- Обработка исключений
- Swagger документация

#### 2. RecommendationService
- Оркестрация проверки бизнес-правил
- Агрегация результатов рекомендаций
- Валидация пользователей
- Статистика и отладка

#### 3. RecommendationRepository
- Доступ к данным через JdbcTemplate
- Оптимизированные SQL запросы
- Конвертация валют (копейки → рубли)
- Безопасная обработка исключений

#### 4. Бизнес-правила (RecommendationRule)
- Invest500Rule: рекомендация Invest 500
- TopSavingRule: рекомендация Top Saving  
- SimpleCreditRule: рекомендация Простого кредита

## Бизнес-правила рекомендаций

### Invest 500
1. Пользователь использует как минимум один продукт с типом DEBIT
2. Пользователь не использует продукты с типом INVEST
3. Сумма пополнений продуктов с типом SAVING больше 1000 ₽

### Top Saving
1. Пользователь использует как минимум один продукт с типом DEBIT
2. Сумма пополнений по всем продуктам типа DEBIT >= 50000 ₽ ИЛИ
   Сумма пополнений по всем продуктам типа SAVING >= 50000 ₽
3. Сумма пополнений по всем продуктам типа DEBIT > суммы трат по всем продуктам типа DEBIT

### Простой кредит
1. Пользователь не использует продукты с типом CREDIT
2. Сумма пополнений по всем продуктам типа DEBIT > суммы трат по всем продуктам типа DEBIT
3. Сумма трат по всем продуктам типа DEBIT > 100000 ₽

## API документация

### Основные эндпойнты
- `GET /api/recommendation/{userId}` - получение рекомендаций для пользователя
- `GET /api/recommendation/{userId}/statistics` - статистика по пользователю
- `GET /api/recommendation/test` - тестирование на предопределенных пользователях

### Тестовые пользователи
Для тестирования доступны три предопределенных пользователя:
- cd515076-5d8a-44be-930e-8d4fcb79f42d - соответствует правилам Invest 500
- d4a4d619-9a0c-4fc5-b0cb-76c49409546b - соответствует правилам Top Saving
- 1f9b149c-6577-448a-bc94-16bea229b71a - соответствует правилам Простого кредита

## Запуск приложения
### Предварительные требования
- Java 17 или выше
- Maven 3.6 или выше
- Файл базы данных transactions.mv.db в корне проекта

## Доступ к интерфейсам
- Приложение: http://localhost:8080/api/recommendation/{userId}
- Swagger UI: http://localhost:8080/api/swagger-ui.html
- H2 Console: http://localhost:8080/api/h2-console

## Структура проекта
src/main/java/com/cw/starbank/recommendation_service/
- RecommendationApplication.java
- config/           # Конфигурационные классы
- controller/       # REST контроллеры
- service/          # Сервисный слой
- repository/       # Репозитории для доступа к данным
- rule/            # Бизнес-правила рекомендаций
- dto/             # Data Transfer Objects
- util/            # Утилитные классы и константы
