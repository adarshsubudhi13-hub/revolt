# REVOLT: Technical Requirements Document (TRD)

## 1. System Architecture Overview
The platform uses a unified, single-port monolithic Spring Boot backend serving both REST endpoints and static HTML/CSS/JavaScript client assets from `src/main/resources/static/`.

```
+-----------------------------------------------------------+
|              Client Layer (Mobile-First Web)              |
|        Vanilla HTML5, CSS3 Custom Properties, ES6 JS      |
+-----------------------------+-----------------------------+
                              | REST / Bearer JWT
+-----------------------------v-----------------------------+
|              Spring Boot 3.3.5 Application                |
|  - Spring Security (Stateless JWT Filter, BCrypt)         |
|  - Controllers: Auth, TradeIn, Products, Orders, Agent...  |
|  - Services: ValuationEngine, PointsLedger, Inspection...  |
|  - Repositories: Spring Data JPA                          |
+-----------------------------+-----------------------------+
                              | JDBC
+-----------------------------v-----------------------------+
|                   Database Storage                        |
|  - Dev: Embedded H2 File Database (Zero-config out of box)|
|  - Prod: PostgreSQL 15+ / MySQL 8.0                       |
+-----------------------------------------------------------+
```

## 2. Layered Architecture & Java Package Structure
- `com.revolt.config`: SecurityConfig, JwtTokenProvider, JwtAuthenticationFilter, WebConfig, DataInitializer
- `com.revolt.model`: User, Role, TradeInDevice, Product, Order, PointsLedger, InspectionReport, OtpVerification, EmailLog
- `com.revolt.repository`: JpaRepository interfaces with custom query methods
- `com.revolt.dto`: Request/Response transfer objects
- `com.revolt.service`: Business logic, valuation algorithms, wallet ledger, mail dispatch
- `com.revolt.controller`: REST APIs

## 3. Asynchronous Email System Design
- Backed by Spring `@Async` and JavaMail.
- Every transactional event (Registration, Order Confirmation, Out For Delivery, Verified, Mismatch Report) logs to `email_logs` table with status (`SENT`, `FAILED`, `SENT_DEV_SIMULATED`).

## 4. File Upload & Storage Strategy
- Handled by `FileStorageService` saving to `./uploads/` with UUID-based filenames.
- Validates mime types (`.jpg`, `.jpeg`, `.png`, `.webp`) and enforces 15MB file size limits.
