# REVOLT: Backend Schema & REST API Contract

## 1. Relational Entity Schema
- **users:** `id` (PK), `full_name`, `email` (UQ), `phone_number` (UQ), `password_hash`, `role` (CUSTOMER, AGENT, ADMIN), `loyalty_tier` (SILVER, GOLD, PLATINUM), `trust_score`, `available_points`, `held_points`, `is_email_verified`, `is_phone_verified`, `created_at`
- **trade_in_devices:** `id` (PK), `user_id` (FK), `category`, `brand`, `model`, `specs_json`, `age_months`, `condition_grade` (GRADE_A, GRADE_B, GRADE_C, GRADE_D), `imei`, `has_original_box`, `has_original_charger`, `has_valid_bill`, `estimated_points`, `locked_until`, `status` (VALUATED, APPLIED_TO_ORDER, VERIFIED, MISMATCH, CANCELLED), `created_at`
- **device_photos:** `id` (PK), `trade_in_device_id` (FK), `photo_type` (FRONT, BACK, SCREEN_ON, SIDES, IMEI_SCREEN, AGENT_INSPECTION), `file_url`, `uploaded_at`
- **products:** `id` (PK), `title`, `category`, `brand`, `description`, `price`, `stock_quantity`, `image_url`, `specs_json`, `rating`, `review_count`, `is_active`, `created_at`
- **orders:** `id` (PK), `order_number` (UQ), `user_id` (FK), `agent_id` (FK), `product_id` (FK), `product_title`, `product_price`, `quantity`, `trade_in_device_id` (FK, nullable), `total_amount`, `points_redeemed`, `cash_paid`, `order_status` (PLACED, CONFIRMED, PACKED, SHIPPED, OUT_FOR_DELIVERY, DELIVERED, CANCELLED, RETURNED), `payment_status` (PENDING, PAID, FAILED, REFUNDED), `shipping_address_json`, `handover_otp`, `created_at`, `delivered_at`
- **points_ledger:** `id` (PK), `user_id` (FK), `order_id` (FK, nullable), `trade_in_device_id` (FK, nullable), `points`, `transaction_type` (EARNED, ON_HOLD, REDEEMED, CANCELLED, EXPIRED), `balance_after`, `description`, `created_at`
- **inspection_reports:** `id` (PK), `order_id` (FK), `trade_in_device_id` (FK), `agent_id` (FK), `imei_matched`, `physical_grade`, `is_approved`, `mismatch_reason`, `revised_points`, `additional_cash_collected`, `agent_notes`, `agent_photos_json`, `inspected_at`
- **otp_verifications:** `id` (PK), `identifier`, `otp_code`, `otp_type` (REGISTRATION, LOGIN, HANDOVER), `expires_at`, `is_used`, `created_at`
- **email_logs:** `id` (PK), `recipient`, `subject`, `email_type`, `content`, `status`, `error_message`, `timestamp`

## 2. Complete REST API Specifications
- `POST /api/auth/register`: Register user and send dual OTPs
- `POST /api/auth/verify-otp`: Verify 6-digit OTP and issue JWT
- `POST /api/auth/login`: Authenticate and issue JWT
- `POST /api/tradein/calculate-valuation`: Public valuation engine calculator
- `POST /api/tradein/submit-listing`: Save device listing with photos and issue 7-day price lock
- `GET /api/tradein/my-devices`: Get current user's registered trade-ins
- `GET /api/products`: Catalog listing with search & category filters
- `POST /api/orders`: Place order, hold points, reserve inventory, issue handover OTP
- `GET /api/orders`: List orders for current customer
- `GET /api/orders/{id}`: Detailed order status and mismatch inspection report
- `GET /api/agent/assigned-orders`: Get deliveries assigned to agent
- `GET /api/agent/inspection-details/{id}`: Get doorstep checklist and declared photos
- `POST /api/agent/submit-inspection`: Submit doorstep verification & complete delivery
- `GET /api/admin/dashboard-stats`: GMV, orders, eco metrics, and points redeemed
- `POST /api/upload`: Multipart image upload (JPEG, PNG, WEBP)
