# Spring Boot OTP + JWT Backend (Ready Code)

This module adds a Spring Boot implementation for:
- OTP send/verify with provider modes (`demo`, `msg91`, `firebase`)
- JWT login + refresh token session handling
- Customer table sessions compatible with existing frontend OTP flow

## Tech stack
- Spring Boot 3.4
- Spring Security + JWT (jjwt)
- PostgreSQL + JPA + Flyway
- MSG91 integration (WebClient)
- Firebase Admin token verification

## APIs

### OTP + Session (frontend-compatible)
- `POST /api/auth/otp/send`
- `POST /api/auth/otp/verify`
- `GET /api/auth/session/{table}`
- `POST /api/auth/firebase/verify` (Firebase phone-auth token flow)

### JWT Auth + Session
- `POST /api/auth/login`
- `POST /api/auth/refresh`
- `POST /api/auth/logout`
- `GET /api/auth/me`

## Default users
Migration seeds two users (password: `Password@123`):
- `anilchowdarya8@gmail.com` (ADMIN)
- `staff@sunsetcafe.local` (STAFF)

Role access is email-based:
- `ADMIN_ACCESS_EMAIL` -> `ADMIN`
- `STAFF_ACCESS_EMAIL` -> `STAFF`
- any other email -> `CUSTOMER`

## Quick start

```bash
cd /Users/anilkumarthammineni/Downloads/cafe-webapp/backend-spring
cp .env.example .env
```

Update `.env` values as needed.

```bash
cd /Users/anilkumarthammineni/Downloads/cafe-webapp/backend-spring
mvn spring-boot:run
```

Server default: `http://localhost:4100`

## OTP providers

### 1) Demo (no external SMS)
- `OTP_PROVIDER=demo`
- `otp/send` response includes `demoCode`

### 2) MSG91 (real SMS)
- `OTP_PROVIDER=msg91`
- Fill:
  - `MSG91_AUTH_KEY`
  - `MSG91_SENDER_ID`
  - `MSG91_TEMPLATE_ID`

### 3) Firebase (real OTP via client SDK)
- `OTP_PROVIDER=firebase`
- Client app performs Firebase phone auth
- Backend verifies ID token at `POST /api/auth/firebase/verify`
- Set `FIREBASE_SERVICE_ACCOUNT_PATH`

## Example payloads

### OTP send
```json
{ "table": "Table 1", "phone": "+919876543210" }
```

### OTP verify
```json
{ "table": "Table 1", "phone": "+919876543210", "code": "123456" }
```

### Login
```json
{ "email": "staff@sunsetcafe.local", "password": "Password@123" }
```


