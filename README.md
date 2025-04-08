# QR Code Generation Service

## ✨ Features

* **Dynamic QR Code Generation:** Creates QR codes via a REST API endpoint (`GET /api/qrcode`).
* **Customizable Output:** Supports parameters for `content`, image `size`, image `type` (PNG, JPG, GIF), and error `correctionLevel` (L, M, Q, H).
* **Input Validation:** Robust validation for all request parameters.
* **API Documentation:** Interactive API documentation available through Swagger UI (via SpringDoc OpenAPI).
* **Persistence:** Stores metadata (content, parameters, timestamp) of generated codes in a database using Spring Data JPA (H2 in-memory by default).
* **Security:** API endpoints secured using Spring Security with database-backed authentication (HTTP Basic for now). Includes password hashing (BCrypt).
* **Caching:** Basic response caching implemented for the QR code generation process to improve performance on repeated requests.
* **Error Handling:** Centralized global exception handling returning consistent JSON error responses.
* **Testing:** Comprehensive Unit and Integration tests (JUnit 5, Mockito, MockMvc, Spring Security Test).
* **Containerization:** Includes a multi-stage `Dockerfile` for building and running the application in a Docker container.
## 🚀 Technologies Used

* **Language:** Java 21
* **Framework:** Spring Boot 3.4.4
    * Spring Web (MVC, REST Controllers)
    * Spring Data JPA (Data Access)
    * Spring Security (Authentication, Authorization)
    * Spring Boot Starter Validation
    * Spring Boot Starter Cache
    * Spring Boot Starter Test (JUnit 5, Mockito, AssertJ, MockMvc, Spring Security Test)
* **Database:**
    * H2 Database (In-Memory for Development/Testing)
    * Hibernate (JPA Provider)
* **API Documentation:** SpringDoc OpenAPI (`swagger-ui`)
* **QR Code Library:** ZXing (Core, Java SE Extensions)
* **Build Tool:** Gradle 8.x
* **Containerization:** Docker


## ⚙️ Configuration
* **Key configuration options are located in src/main/resources/application.properties.**
  * Server Port: server.port (Defaults to 8080)
  * Database: H2 in-memory database is used by default (spring.datasource.url=jdbc:h2:mem:qrcodedb).
  * H2 Console: Enabled by default at http://localhost:8080/h2-console.
  * Use JDBC URL: jdbc:h2:mem:qrcodedb
  * Username: sa
  * Password: password
  * JPA Schema: spring.jpa.hibernate.ddl-auto=update (Automatically updates schema based on entities - development only).
  * QR Defaults: Default size, type, correctionLevel can be adjusted (qrcode.default.*).
  * Security: A default user (user/password) is created on startup if not present in the database (see QrCodeServiceApplication.java or equivalent initializer). Passwords are BCrypt encoded.
 
## 📖 API Documentation & Usage
* **Interactive API documentation is generated using SpringDoc OpenAPI and is available via Swagger UI while the application is running:**
    * Swagger UI URL: http://localhost:8080/swagger-ui.html
    * Authentication
 * **The API uses HTTP Basic Authentication. You must provide valid credentials with your requests. Use the default user created on startup:**
      * Username: user
      * Password: password
      * Example Request (curl)
      * Bash
          * curl -u user:password "http://localhost:8080/api/qrcode?content=Hello%20Secure%20World&type=png&size=250&correctionLevel=Q" -o qr_code.png
          * This command will authenticate, generate the QR code, and save it as qr_code.png.

## 🗃️ Database (Development)
* **By default, an H2 in-memory database is used. Data persists only as long as the application is running.**
You can access the H2 Console at http://localhost:8080/h2-console to view tables (USERS, USER_AUTHORITIES, QR_CODE_RECORDS) and run SQL queries.
  * JDBC URL: jdbc:h2:mem:qrcodedb
  * Username: sa
  * Password: password
    * Configuration should be changed for production deployment in application.properties to connect to a persistent database like PostgreSQL, and schema management should be handled via migration tools.
## ✅ Testing
The project includes both unit and integration tests.

Run all tests using the Gradle wrapper:
Bash

# Linux/macOS
./gradlew test

# Windows
.\gradlew.bat test
