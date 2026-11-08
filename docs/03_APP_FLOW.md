# REVOLT: App Flow & State Machine Document

## 1. End-to-End Customer Flow
```mermaid
flowchart TD
    A[Customer Registers] --> B[Dual OTP Verification]
    B --> C[Active Account Created]
    C --> D[List Old Device & Condition]
    D --> E[7-Day Instant Price Lock Issued]
    E --> F[Browse Catalog & Add New Product to Cart]
    F --> G[Apply Trade-In Points - Max 30% Cap]
    G --> H[Checkout - Points Placed ON HOLD]
    H --> I[Order Status: OUT_FOR_DELIVERY]
    I --> J{Doorstep Inspection}
    J -- Match --> K[Points Finalized - REDEEMED]
    K --> L[Order DELIVERED]
    J -- Mismatch --> M[Discrepancy Report Issued]
    M --> N[Held Points Revoked & Cash Delta Collected]
    N --> L
```

## 2. Order Lifecycle State Machine
- `PLACED`: Order registered; trade-in points placed ON_HOLD in ledger.
- `CONFIRMED`: Warehouse inventory reserved.
- `PACKED`: Shipment packaged with tracking tag.
- `SHIPPED`: In-transit to regional hub.
- `OUT_FOR_DELIVERY`: Assigned to field agent; 4-digit Handover Secret OTP issued to customer.
- `DELIVERED`: Physical doorstep inspection completed and OTP validated.
- `CANCELLED`: Order aborted; points restored to wallet.
