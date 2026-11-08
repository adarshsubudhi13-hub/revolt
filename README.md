# ReVolt

**Trade in your old electronics. Earn points. Buy new for less.**

live link-[http://localhost:8085](http://localhost:8085)

ReVolt is a buy-and-sell platform for electronics. Users list their old devices with photos, earn reward points based on the device's value, and redeem those points to get a discount on new products from the same store. A delivery agent verifies the old device at handover, and every order update is emailed to the customer automatically.

---

## Table of Contents

1. [How It Works](#how-it-works)
2. [Features](#features)
3. [Tech Stack](#tech-stack)
4. [Project Structure](#project-structure)
5. [Getting Started](#getting-started)
6. [Configuration](#configuration)
7. [Frontend and Backend Integration](#frontend-and-backend-integration)
8. [API Overview](#api-overview)
9. [Order and Trade-in Lifecycle](#order-and-trade-in-lifecycle)
10. [Email Notifications](#email-notifications)
11. [Business Rules](#business-rules)
12. [Roadmap](#roadmap)
13. [Contributing](#contributing)
14. [License](#license)

---

## How It Works

1. **Register** with name, phone number and email. Both are verified with an OTP.
2. **List your old device** with its details (brand, model, age, condition, IMEI) and photos.
3. **Earn points.** The valuation engine converts the device's estimated value into reward points, which are held until the device is verified.
4. **Shop.** Add new products to the cart and apply points at checkout to reduce the price.
5. **Delivery and verification.** The agent delivers the new product and inspects the old device against your photos and details.
   - **Match:** points are finalized and you keep the discount.
   - **Mismatch:** the agent still collects the old device, the held points are cancelled, and you pay the full price of the new product.
6. **Stay informed.** Order confirmation, shipping and delivery updates arrive by email.

---

## Features

### Core
- [ ] Phone and email registration with OTP verification
- [ ] JWT-based login and role-based access (Customer, Delivery Agent, Admin)
- [ ] Device listing with multi-photo upload and condition questionnaire
- [ ] Rule-based valuation engine, configurable by admin
- [ ] Points wallet with a full transaction ledger (earned, on hold, redeemed, cancelled, expired)
- [ ] Product catalog with categories, brands, search, filters and sorting
- [ ] Cart, wishlist and checkout with points redemption
- [ ] Payment gateway integration (test mode during development)
- [ ] Order management with a status state machine
- [ ] Delivery agent portal with an inspection checklist and photo capture
- [ ] Admin dashboard for products, orders, agents, valuation rules and disputes
- [ ] Automatic email notifications for every order stage

### What Makes ReVolt Different
- [ ] **Price Lock:** the quoted trade-in value is locked for 7 days
- [ ] **Device Health Passport:** a digital condition report for each listed device
- [ ] **Smart Photo Guide:** guided photo capture with an on-screen checklist
- [ ] **IMEI/serial validation** and duplicate-listing detection to prevent fraud
- [ ] **Mismatch Transparency Report:** side-by-side comparison of user photos and agent photos with a reason
- [ ] **Trust Score:** good history unlocks higher points limits and bonus points
- [ ] **Eco Impact Dashboard:** e-waste diverted and CO2 saved per user
- [ ] **Loyalty tiers** (Silver, Gold, Platinum) with bonus point multipliers
- [ ] **Accessory bonus:** extra points for including the charger, box and bill
- [ ] **Referral program** that rewards both users
- [ ] Product comparison, price-drop alerts and live order tracking
- [ ] Dispute and appeal flow for mismatches (within 48 hours)

*Later phases: refurbished marketplace, support chat with FAQ bot, multi-language (English, Telugu, Hindi), dark mode.*

---

## Tech Stack

| Layer | Technology |
|-------|------------|
| Backend | Java 17+, Spring Boot, Spring Security (JWT), Spring Data JPA / Hibernate, Spring Mail |
| Build tool | Maven |
| Database | MySQL or PostgreSQL |
| Frontend | HTML5, CSS3, JavaScript (no framework) |
| Email | SMTP (Gmail for development, SendGrid or Brevo for production), Thymeleaf templates |
| OTP | SMS provider (Twilio, MSG91 or Firebase) and email OTP |
| Payments | Razorpay (test mode) |
| File storage | Local disk or Cloudinary / S3 |

> The backend is **Java only**. The frontend is plain HTML, CSS and JavaScript that talks to the backend through REST APIs.

---

## Project Structure

```
revolt/
├── backend/
│   ├── pom.xml
│   └── src/main/
│       ├── java/com/revolt/
│       │   ├── RevoltApplication.java
│       │   ├── config/          # Security, CORS, mail, async, file upload
│       │   ├── controller/      # REST controllers
│       │   ├── service/         # Business logic (valuation, points, orders, email)
│       │   ├── repository/      # Spring Data JPA repositories
│       │   ├── entity/          # JPA entities
│       │   ├── dto/             # Request / response objects
│       │   ├── security/        # JWT filter, user details
│       │   ├── exception/       # Global exception handling
│       │   └── util/
│       └── resources/
│           ├── application.properties
│           └── templates/email/ # HTML email templates
│
├── frontend/
│   ├── index.html
│   ├── pages/                   # login, register, sell, shop, cart, orders, wallet, admin, agent
│   ├── css/
│   ├── js/
│   │   ├── api.js               # fetch wrapper (base URL, auth header, error handling)
│   │   ├── auth.js
│   │   ├── sell.js
│   │   ├── shop.js
│   │   ├── cart.js
│   │   └── orders.js
│   └── assets/
│
├── docs/                        # PRD, TRD, App Flow, UI/UX Brief, Schema, Implementation Plan
└── README.md
```

---

## Getting Started

### Prerequisites
- JDK 17 or later
- Maven 3.8+
- MySQL 8 or PostgreSQL 14+
- A Gmail account with an App Password (or another SMTP provider) for emails
- A modern browser

### 1. Clone the repository
```bash
git clone <your-repo-url>
cd revolt
```

### 2. Create the database
```sql
CREATE DATABASE revolt;
```

### 3. Configure the backend
Edit `backend/src/main/resources/application.properties` (see [Configuration](#configuration)).

### 4. Run the backend
```bash
cd backend
mvn spring-boot:run
```
The API starts at `http://localhost:8080`.

### 5. Run the frontend
Open `frontend/index.html` through a local server such as the VS Code Live Server extension (port 5500), or:
```bash
cd frontend
python -m http.server 5500
```
Then visit `http://localhost:5500`.

---

## Configuration

`application.properties` (use environment variables for secrets, and never commit real credentials):

```properties
server.port=8080

# Database
spring.datasource.url=jdbc:mysql://localhost:3306/revolt
spring.datasource.username=${DB_USER}
spring.datasource.password=${DB_PASSWORD}
spring.jpa.hibernate.ddl-auto=update

# JWT
app.jwt.secret=${JWT_SECRET}
app.jwt.expiration-ms=86400000

# Email (SMTP)
spring.mail.host=smtp.gmail.com
spring.mail.port=587
spring.mail.username=${MAIL_USER}
spring.mail.password=${MAIL_APP_PASSWORD}
spring.mail.properties.mail.smtp.auth=true
spring.mail.properties.mail.smtp.starttls.enable=true

# File uploads
spring.servlet.multipart.max-file-size=5MB
spring.servlet.multipart.max-request-size=30MB
app.upload.dir=uploads/

# CORS
app.cors.allowed-origins=http://localhost:5500

# Points
app.points.rate=1
app.points.max-redeem-percent=50
```

---

## Frontend and Backend Integration

- All frontend calls go through `js/api.js`, which sets the base URL (`http://localhost:8080/api`), attaches the `Authorization: Bearer <token>` header, and handles errors in one place.
- CORS is configured in the backend `config` package to allow the frontend origin.
- Email notifications are triggered on the **backend** whenever an order or trade-in status changes, so the frontend never has to send emails itself.
- Error responses use a consistent JSON shape:
```json
{ "timestamp": "...", "status": 400, "error": "Bad Request", "message": "Details here" }
```

---

## API Overview

Base path: `/api`

| Area | Endpoints (examples) |
|------|----------------------|
| Auth | `POST /auth/register`, `POST /auth/verify-otp`, `POST /auth/login`, `POST /auth/forgot-password` |
| Users | `GET /users/me`, `PUT /users/me`, `GET /users/me/addresses` |
| Devices | `POST /devices` (with photos), `GET /devices/{id}`, `GET /devices/mine` |
| Valuation | `POST /valuation/estimate` |
| Wallet | `GET /wallet`, `GET /wallet/transactions` |
| Products | `GET /products`, `GET /products/{id}`, `GET /categories` |
| Cart | `GET /cart`, `POST /cart/items`, `DELETE /cart/items/{id}` |
| Orders | `POST /orders`, `GET /orders/mine`, `GET /orders/{id}`, `POST /orders/{id}/cancel` |
| Agent | `GET /agent/orders`, `POST /agent/orders/{id}/inspection`, `POST /agent/orders/{id}/deliver` |
| Admin | `/admin/products`, `/admin/orders`, `/admin/agents`, `/admin/valuation-rules`, `/admin/disputes` |
| Disputes | `POST /orders/{id}/dispute` |

The full contract (request and response bodies, status codes) lives in `docs/backend-schema.md`.

---

## Order and Trade-in Lifecycle

```
PLACED -> CONFIRMED -> PACKED -> SHIPPED -> OUT_FOR_DELIVERY -> DELIVERED
                                                                   |
                                          +------------------------+------------------------+
                                          |                                                 |
                                TRADE-IN VERIFIED                                   TRADE-IN MISMATCH
                                (points finalized)                      (points cancelled, full price charged)
```

Orders can also move to `CANCELLED` or `RETURNED` according to the refund policy.

---

## Email Notifications

Emails are sent asynchronously with retry, and each one is recorded in an `email_logs` table.

- Registration and email verification
- OTP codes and password reset
- Device listing submitted and points credited (on hold)
- Order placed, confirmed and payment received
- Packed, shipped (with tracking info), out for delivery, delivered
- Trade-in verified and points finalized
- Trade-in mismatch, with the revised amount and reason
- Order cancelled or refunded
- Points expiry reminders

---

## Business Rules

- Points are credited as **on hold** and become final only after the agent verifies the device.
- A maximum percentage of any order can be paid with points (admin configurable).
- The trade-in quote is price-locked for 7 days.
- On a full mismatch, held points are cancelled and the customer pays the full price of the new product.
- On a partial mismatch, points are adjusted instead of cancelled entirely.
- Customers can raise a dispute within 48 hours of a mismatch, with evidence.
- Passwords are hashed with BCrypt. Phone numbers are masked in agent and tracking views.

---

## Roadmap

- **Phase 1 (MVP):** auth with OTP, device listing, valuation, points wallet, catalog, cart, checkout, orders, email notifications, admin basics
- **Phase 2:** agent portal, mismatch report, disputes, trust score, loyalty tiers, referrals, price alerts
- **Phase 3:** refurbished marketplace, support chat, multi-language, dark mode, analytics

---

## Contributing

1. Fork the repository
2. Create a feature branch: `git checkout -b feature/your-feature`
3. Commit your changes: `git commit -m "Add your feature"`
4. Push to the branch: `git push origin feature/your-feature`
5. Open a pull request

---

## License

Add your license here (for example, MIT).
