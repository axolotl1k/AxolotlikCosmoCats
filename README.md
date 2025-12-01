# 🪐 Marketplace API (Spring Boot)

Лабораторна робота з дисципліни **"Web-технології (Java)"**

## 📘 Опис

Цей проєкт реалізує REST API для управління товарами на маркетплейсі космічних котиків 💾.  
На даному етапі реалізовано базову архітектуру, документацію API через Swagger UI,  
валидацію вхідних даних (у тому числі кастомну), централізовану обробку помилок і повне тестове покриття.

---

## ⚙️ Технології

- **Java 21**
- **Spring Boot 3.3.4**
- **Gradle**
- **MapStruct** — мапінг між доменними об'єктами та DTO
- **Lombok** — скорочення шаблонного коду
- **Jakarta Bean Validation** — валідація вхідних даних
- **Swagger / OpenAPI** — документація та тестування API
- **JUnit 5**, **Mockito**, **AssertJ**, **MockMvc**, **Testcontainers**, **WireMock** — для тестування
- **JaCoCo** — перевірка покриття коду
- **GitHub Actions** — CI/CD перевірка збірки та тестів

---

## 🧩 Архітектура

```
src/
├── main/
│ ├── java/org/axolotlik/axolotlikcosmocats/
│ │ ├── common/                             # Загальні утиліти, enum-и
│ │ ├── config/                             # Конфігурації Spring / Swagger / FeatureToggle
│ │ ├── domain/                             # Доменні моделі (чисті Java об'єкти)
│ │ ├── dto/                                # DTO для API
│ │ ├── featuretoggle/                      # Система Feature Toggles (AOP)
│ │ ├── repository/                         # Шар доступу до даних + Інтерфейси Spring Data JPA
│ │ │   ├── entity/                         # JPA Entities (відповідають таблицям БД)
│ │ │   └── projection/                     # JPA Projections (для звітів)
│ │ ├── service/                            # Бізнес-логіка (@Transactional)
│ │ │   ├── impl/                           # Реалізація сервісів
│ │ │   ├── exception/                      # Винятки
│ │ │   └── mapper/                         # Мапери DTO <-> Domain та Domain <-> Entity
│ │ ├── util/                               # Кастомні валідації
│ │ └── web/                                # REST контролери, обробка помилок
│ └── resources/
│   ├── api-specs/                          # Swagger контракт
│   └── db/changelog/                       # Liquibase міграції (XML/YAML)
│
└── test/java/org/axolotlik/axolotlikcosmocats/
    ├── featuretoggle/                      # Тести для Feature Toggles
    ├── service/impl/                       # Unit-тести сервісів (Mockito)
    └── web/                                # Integration-тести (Testcontainers + MockMvc)
```

---

## 🚀 Запуск проєкту

```bash
./gradlew bootRun
```

Після запуску застосунок буде доступний за адресою:

> http://localhost:8080/swagger-ui/index.html

---

## ✅ Реалізовано

### 🧱 Архітектура застосунку

- Побудовано трирівневу структуру:
    - **web** — REST контролери та глобальна обробка помилок
    - **service** — бізнес-логіка, управління даними
    - **repository** — власні In-Memory сховища (емуляція бази даних)
    - **domain** — доменні сутності (Product, Category, Cart, Order)
    - **dto** — об’єкти для передачі даних між шарами

---

### 🧩 Репозиторії

- Реалізовано **власні In-Memory репозиторії** для роботи без БД (через `Map`).
- Підтримують CRUD-операції: `save`, `findById`, `findAll`, `update`, `deleteById`.
- Використовуються сервісним шаром як абстракція доступу до даних.

---

### ⚙️ Сервісний шар

- Реалізовано базові **CRUD-операції** для основних сутностей.
- Уся логіка роботи з репозиторіями винесена в сервіси.

---

### 🌐 REST API

- Створено контролери для управління сутностями (`/products`, `/categories`, `/orders`, `/carts`).
- Обробка запитів через **DTO** із валідацією вхідних даних.
- Реалізовано ендпоінти:
    - `GET` — отримання списків та окремих об’єктів
    - `POST` — створення нових ресурсів
    - `PUT` — оновлення (повне)
    - `PATCH` — оновлення (часткове)
    - `DELETE` — видалення

---

### ✅ Валідація

- Підключено **Jakarta Bean Validation** для DTO.
- Додано **кастомну валідацію** `@AtLeastOneNonEmpty` для `CartUpdateRequestDto`.
- Додано **кастомну валідацію** `@CosmicWordCheck` для `ProductRequestDto` поля `name`.

---

### 🚨 Обробка помилок

- Створено **глобальний обробник помилок** `GlobalExceptionHandler`.
- Підтримуються:
    - `NotFoundException` (404)
    - `MethodArgumentNotValidException` (400)
- Формат помилки: `ProblemDetail` із полем `errors` (список порушень валідації).

---

## 🧪 Тестування (Лабораторна 1.2)

- Покриття **unit-тестами всіх сервісів** (Cart, Category, Product, Order)
- Реалізовано **інтеграційні тести для кожного контролера**:
- Повна перевірка CRUD-операцій, валідацій і викидання винятків
- Використано:
    - **JUnit 5** — основний тестовий фреймворк
    - **Mockito / @MockBean** — для мокування залежностей
    - **AssertJ** — розширені асерти для зручності перевірок
    - **MockMvc** — інтеграційне тестування REST API
    - **JaCoCo** — збір покриття коду
    - **Spring Boot Test** — для підйому контексту Spring
- Загальне покриття коду — **~90%**

---

## 📊 Звіт покриття коду

**IntegrationTestCoverage**

```
Test Coverage:
    - Class Coverage: 100%
    - Method Coverage: 95.9%
    - Branch Coverage: 67.6%
    - Line Coverage: 94.8%
    - Instruction Coverage: 95.6%
    - Complexity Coverage: 81.8%
``` 

**TestCoverage**

```
Test Coverage:
    - Class Coverage: 100%
    - Method Coverage: 100%
    - Branch Coverage: 70.3%
    - Line Coverage: 96.4%
    - Instruction Coverage: 97.8%
    - Complexity Coverage: 86.2%
```

---

## ⚙️ Запуск тестів

```bash
./gradlew clean test
```

Звіт про покриття:

```
build/reports/coverage/index.html
```

---

## 🚀 Лабораторна 2 — Feature Toggles (Spring AOP)

### 🎯 Мета

Реалізувати систему **Feature Toggle** для керування функціональністю застосунку залежно від середовища (environment).

---

### ⚙️ Реалізація

* Додано **`FeatureToggleService`** для зберігання та керування станом фіч.
* Налаштовано **`FeatureToggleProperties`**, що читає YAML-конфігурацію (`application.feature.*`).
* Створено **аспект `FeatureToggleAspect`**, який:

    * перевіряє активність фіч перед виконанням методу;
    * кидає `FeatureNotAvailableException`, якщо фіча вимкнена;
    * інтегрований через Spring AOP (`@Aspect`).

---

### 🐱 Новий функціонал

* Створено **CosmoCatsController** з ендпоінтами:

    * `GET /api/v1/galactic-citizen-registry` — отримати всіх котиків (працює лише якщо фіча активна);
    * `GET /api/v1/galactic-citizen-registry/{name}` — отримати конкретного котика;
* Додано виняток **FeatureNotAvailableException** з кодом **503 Service Unavailable**;
* Розширено **GlobalExceptionHandler** для обробки цієї помилки.

---

### 🤪 Тестування (Feature Toggle)

* Створено розширення **`FeatureToggleExtension`** для JUnit 5, яке:

    * автоматично вмикає / вимикає фічу перед кожним тестом;
    * повертає конфігурацію до початкового стану після тесту.
* Додано кастомні анотації:

    * `@EnabledFeatureToggle`
    * `@DisabledFeatureToggle`
* Написано **інтеграційні тести для CosmoCatsController**:

    * ✅ `200 OK` — коли фіча активна;
    * 🚫 `503 Service Unavailable` — коли фіча вимкнена;
    * ⚠️ `404 Not Found` — коли кота не знайдено;

## 🗄️ Лабораторна 3 — Інтеграція з БД

### 1. Проєктування та Міграції (Liquibase)
Схема бази даних керується через **Liquibase**. Реалізовано версіонування змін:
- **`001-initial-schema.yaml`**: Створення таблиць `products`, `categories`, `orders`, `carts` та зв'язків (Foreign Keys). Використано `incrementBy: 50` для оптимізації Sequence генераторів.
- **`002-constraints-and-indexes.yaml`**:
  - **Unique Constraints**: Забезпечено унікальність імені продукту в межах категорії.
  - **Indexes**: Додано індекси для полів, за якими часто йде пошук (`name`).

### 2. Spring Data JPA & Entities
- Створено **JPA сутності** з анотаціями `@Entity`, `@Table`, `@ManyToOne`, `@ManyToMany`.
- Налаштовано **Lazy Loading** для колекцій, щоб уникнути проблем з продуктивністю.
- Реалізовано **Referential Integrity**: видалення категорії, яка містить продукти, блокується базою даних (409 Conflict), що захищає дані від випадкової втрати.

### 3. Транзакції
- Сервісний шар (`ServiceImpl`) анотовано **`@Transactional`**.
- Використано оптимізацію `readOnly = true` для методів читання (`GET`), що покращує швидкодію Hibernate.

### 4. Projections та Custom Queries (+2 бали)
Реалізовано аналітичний звіт "Топ товарів за продажами":
- **JPQL Запит**: Використано агрегацію `COUNT` та групування `GROUP BY` на рівні бази даних.
- **Projections**: Створено інтерфейс `ProductSalesStats`, щоб витягувати з БД тільки необхідні поля (назва + кількість), а не цілі сутності.
- **Ендпоінт**: `GET /api/v1/orders/stats/top?limit=5`

---

## 🐋 Тестування з Docker

Реалізовано повноцінні **інтеграційні тести** з використанням **Testcontainers**:
- Створено базовий клас `AbstractIT`, який піднімає контейнер `postgres:16-alpine`.
- Тести запускаються на **реальній базі даних**, що гарантує сумісність SQL-запитів та міграцій.
- У тестах використовується прямий доступ до репозиторіїв для підготовки даних (Arrange phase), що ізолює тестування контролерів від логіки сервісів.

## ✨ Автор

**Студент групи ІО-35 — Слюсар Олександр**  
**💬 Мій Telegram:** [@axolotlik](https://t.me/a_x_o_l_o_t_l)  
**💻 GitHub:** [axolotl1k](https://github.com/axolotl1k)
