# Currency Converter

![Java](https://img.shields.io/badge/Java-17-orange?style=flat&logo=java)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.3.4-brightgreen?style=flat&logo=spring)
![Gradle](https://img.shields.io/badge/Gradle-9.2.0-blue?style=flat&logo=gradle)
![License](https://img.shields.io/badge/License-MIT-yellow?style=flat)

A high-performance RESTful API service for real-time currency conversion, integrating with the Polish National Bank (NBP) exchange rates, now with an integrated frontend for a complete user experience. Built with modern Spring Boot and designed for reliability and scalability.

## ✨ Features

*   **Real-time Currency Conversion:** Convert between PLN and foreign currencies, or between two foreign currencies, using live exchange rates from the NBP API.
*   **Integrated Frontend:** A user-friendly web interface served directly from the application.
*   **Intelligent Caching:** Automatic caching of exchange rates (configurable TTL) to reduce external API calls and improve response times.

## 🛠 Technology Stack

*   **Backend:** Java 17, Spring Boot 3.3.4 (Spring Web, Spring Cache)
*   **Frontend:** HTML, CSS, JavaScript (served statically from `resources/static`)
*   **Build:** Gradle 9.2.0, Lombok 1.18.26
*   **External API:** Polish National Bank (NBP) API
*   **Testing:** JUnit 5, Mockito, Spring Boot Test, MockMvc

## 🚀 Getting Started

1.  **Clone the repository:**
    ```bash
    git clone https://github.com/andrzej-t/currency-converter.git
    cd currency-converter
    ```
2.  **Build and run:**
    ```bash
    ./gradlew bootRun
    ```
    The application, including the frontend, will be available at `http://localhost:8080`.

## 🔌 API Endpoints

**Base URL:** `http://localhost:8080/v1`

*   **Get All Available Currencies:**
    ```http
    GET /v1/currencies
    ```
*   **Convert Currency:**
    ```http
    GET /v1/result?amount={amount}&currencyFrom={from}&currencyTo={to}
    ```
    Example: `GET /v1/result?amount=100.50&currencyFrom=PLN&currencyTo=USD`

## 👤 Author & Links

*   **Author:** Andrzej Tyrpa (GitHub: @andrzej-t)
*   **Live Demo:**
*   **Frontend Source:** Integrated within this repository (`resources/static`).

## 📄 License

This project is licensed under the MIT License.