# KoloOS MVP v1.0

Monorepo containing the backend API (Node.js/Express + PostgreSQL) and Android mobile client (Kotlin + Jetpack Compose) for the KoloOS SME business management platform.

## Structure

- `backend/` – Express TypeScript API with JWT auth, product and sales management, payment initiation stubs, receipts, and sync event endpoints.
- `android/` – Android app built with Jetpack Compose, Hilt, Retrofit, Room, and WorkManager for offline-first flows.

## Backend

### Getting started

```bash
cd backend
cp .env.example .env
npm install
npm run dev
```

Run the SQL in `backend/db/migrations/001_init.sql` against your PostgreSQL instance before starting the server. Swagger docs are exposed at `/docs` once the app is running.

### Key endpoints

- `POST /api/v1/auth/request-otp` – mock OTP flow for merchants.
- `POST /api/v1/products` – create product tied to authenticated merchant.
- `POST /api/v1/sales` – record sale and deduct inventory transactionally.
- `POST /api/v1/payments/initiate` – generate mock Paystack/Flutterwave payment intent.
- `POST /api/v1/payments/webhook` – PSP webhook (public, signature-verified).
- `POST /api/v1/sync/push` and `/sync/pull` – offline sync queue endpoints.
- `GET /api/v1/receipts/:saleId` – WhatsApp-ready receipt payload.

## Android app

The Android module targets SDK 34 and relies on Jetpack Compose for UI. Key components include:

- `SessionManager` for DataStore-backed JWT persistence.
- `ProductRepository`, `SalesRepository`, and `SyncRepository` bridging Retrofit APIs with Room tables.
- Compose screens for auth, dashboard, product CRUD, and sale capture.
- Offline-first queueing of sales via Room + WorkManager-driven `SyncWorker`.

To open the project, load the `android/` directory in Android Studio (Giraffe+). Configure the base URL in `ApiConfig.kt` to point to your backend instance.

## Example offline flow

1. Merchant signs in with phone + OTP (mocked) and token is stored locally.
2. Products are fetched/synced into Room and listed in the app.
3. A sale recorded offline is added to the local queue and inventory is updated locally.
4. When connectivity resumes, `SyncWorker` pushes pending sales and pulls server-side updates.
5. Once payment is confirmed through the webhook, the sale status syncs back down, enabling WhatsApp receipt sharing.
