# 🪐 Marketplace API (Spring Boot)

Лабораторна робота з дисципліни **"Web-технології (Java)"**

## 📘 Опис

Цей проєкт реалізує REST API для управління товарами на маркетплейсі космічних котиків.

---

## ⚙️ Технології

- **Java 21**
- **Spring Boot 3.3.4**
- **Gradle**
- **MapStruct** — для мапінгу між доменними об'єктами та DTO
- **Lombok** — для скорочення коду моделей
- **Jakarta Bean Validation** — для валідації вхідних даних
- **Swagger / OpenAPI** — для документування API
- **JUnit 5**, **AssertJ**, **WireMock**, **Testcontainers** — для тестування

---

## 🧩 Архітектура

```
src/
├── main/
│ ├── java/org/axolotlik/axolotlikcosmocats/
│ │ ├── common/                             # Загальні утиліти, константи, enum-и
│ │ ├── config/                             # Конфігураційні класи Spring
│ │ ├── domain/                             # Доменні моделі (Product, Category, Order, Cart)
│ │ ├── dto/                                # DTO об’єкти для обміну даними між рівнями
│ │ ├── service/                            # Бізнес-логіка (CRUD-операції, сервіси)
│ │ ├── util/                               # Кастомні валідації, хелпери, спільні методи
│ │ ├── web/                                # REST контролери, обробники помилок
│ │ └── AxolotlikCosmoCatsApplication.java  # Головний клас Spring Boot застосунку
│ └── resources/
│   └── api-specs/                          # Swagger / OpenAPI контракт
└── test/                                   # Тести (юніт, інтеграційні, WireMock)
```

---

## 🚀 Запуск проєкту

```bash
./gradlew bootRun
```

Після запуску застосунок буде доступний за адресою:

> http://localhost:8080


## ✨ Автор

**Студент групи ІО-35 Слюсар Олександр**    
**💬 [Мій Telegram @axolotlik](https://t.me/axolotlik)**

---

## 🧠 Статус

**Лабораторна 1.1:**    
🔜 Частина 1: API Contract та обробка помилок    
🔜 Частина 2: Основи Domain-Driven Design (DDD)  
🔜 Частина 3: CRUD-операції  

---

**Лабораторна 1.2:**    
🔜 Тестування
