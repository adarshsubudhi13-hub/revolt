# REVOLT: Product Requirements Document (PRD)

## 1. Executive Summary
ReVolt is a circular electronics commerce ecosystem designed to eliminate friction in consumer trade-ins. Users trade in their pre-owned consumer electronics to earn instant reward points, apply those points to purchase brand-new electronics at up to a 30% discount, and execute a simultaneous physical swap at their doorstep during delivery.

## 2. User Personas
- **Rohit Sharma (Consumer Upgrader):** Tech-savvy professional looking for hassle-free upgrades without dealing with online marketplace bargaining or delayed mail-in inspection payouts.
- **Suresh Kumar (Field Logistics Agent):** Delivery executive equipped with a mobile-first inspection checklist to verify physical condition, validate IMEIs, and complete handovers within 8 minutes.
- **Anita Desai (Platform Controller / Admin):** Operations lead managing catalog margins, intake warehousing, mismatch disputes, and double-entry wallet reconciliation.

## 3. Core User Stories & Gherkin Scenarios
- **US-1: Registration & Dual-Factor Verification:** User signs up with email & phone, receiving dual OTPs. Accounts activate upon OTP confirmation.
- **US-2: Algorithmic Valuation & 7-Day Price Lock:** User submits specifications and condition questionnaire to receive an instant point quote, protected for 7 days.
- **US-3: Checkout & Points Hold:** Points are put on hold at checkout (capped at 30% of subtotal) and finalized only after doorstep inspection.
- **US-4: Doorstep Audit & Mismatch Resolution:** Field agent inspects physical device against declared specs. If matched, points finalize; if mismatched, discrepancy report is issued and cash delta collected.

## 4. Feature Prioritization Matrix
- **MVP:** Dual-Factor Auth, 5-Step Trade-in Wizard, 7-Day Price Lock, Points Ledger, Catalog & Cart, Razorpay Test Integration, Agent Doorstep Portal, Order Timeline, Mismatch Transparency Report.
- **Phase 2:** Multi-device trade-in bundling, Refurbished marketplace, Tiered loyalty perks, Multilingual localization (Telugu, Hindi), FAQ Chatbot.

## 5. Business Rules & Edge Cases
- 1 ReVolt Point = ₹1 INR. Points are strictly non-cashable.
- Max 30% of order subtotal payable via points.
- 7-Day return policy preserves trade-in points in user wallet (collected devices are not returned once shipped to warehouse).
