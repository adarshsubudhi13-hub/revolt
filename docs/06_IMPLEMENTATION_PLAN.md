# REVOLT: Phased Implementation Plan & Agent Prompts

## 1. Build Milestones & Verification
- **Milestone 1 (Foundation):** Spring Boot 3.3.5, Java 21, Security JWT filter, User entity, Dual OTP service, and Auth REST APIs.
- **Milestone 2 (Valuation Engine & Ledger):** Rule-based pricing matrix, 7-day price lock, points ledger with double-entry accounting.
- **Milestone 3 (Catalog & Cart):** Product catalog, dynamic points redemption slider capped at 30%, address form, and Razorpay test checkout.
- **Milestone 4 (Fulfillment & Inspection):** Agent portal, doorstep checklist, IMEI verification, mismatch transparency reports, and transactional email logs.
- **Milestone 5 (Admin Control & Polish):** Admin operations dashboard, GMV KPIs, eco impact counter, and dark/light theme engine.

## 2. Definition of Done (DoD)
- Clean Maven build (`mvn clean compile`) with zero compilation errors.
- Out-of-the-box local execution on port 8080 without requiring external databases or cloud accounts.
- Seed data available immediately upon startup with 3 ready personas (Customer, Agent, Admin) for immediate end-to-end testing.
