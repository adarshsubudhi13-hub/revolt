# REVOLT: UI/UX Design Brief & Frontend Architecture

## 1. Design Tokens & Styling Rules
- **Primary Color:** `#059669` (Eco Green), Hover `#047857`
- **Accent Color:** `#2563eb` (Royal Blue)
- **Warning / Points Color:** `#f59e0b` (Gold / Amber)
- **Danger / Mismatch Color:** `#ef4444` (Crimson)
- **Typography:** System font stack (-apple-system, BlinkMacSystemFont, Segoe UI, Roboto)
- **Theme Support:** Dark & Light modes with persistent `localStorage` preference

## 2. Key Page Wireframe Specifications
1. `index.html`: Landing page with Quick Estimator widget, real-time eco impact statistics counter, and featured store catalog.
2. `tradein.html`: 5-step guided wizard (Device Specs -> Condition -> Accessories -> Photos -> Price Lock Quote).
3. `shop.html`: Electronics catalog with category chips, keyword search, price sorting, and dynamic points discount hint pills.
4. `cart.html`: Cart items manager with interactive points redemption slider enforcing the 30% discount cap.
5. `checkout.html`: Address collection, points hold confirmation, and simulated Razorpay test mode modal.
6. `orders.html`: Customer tracking timeline with secret 4-digit handover OTP badge and mismatch transparency side-by-side photo comparison modal.
7. `agent.html`: Mobile-first field portal with IMEI verification, physical condition checklist, camera proof upload, and handover OTP validator.
8. `admin.html`: Operations control center with GMV KPIs, orders management, mismatch dispute reviews, and email log audit trail.
9. `login.html`: Auth portal featuring 1-click test login for Customer, Agent, and Admin, plus dual-OTP registration.
