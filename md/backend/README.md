# Cafe Web App Backend (Phase 1)

This backend replaces browser `localStorage` with APIs and PostgreSQL.

## Stack
- Language: TypeScript (Node.js 20+)
- Web framework: Fastify
- ORM and migrations: Prisma
- Database: PostgreSQL
- Dev runtime: `tsx`
- Build tool: TypeScript compiler (`tsc`)

## Quick start
1. Copy `.env.example` to `.env` and update values.
2. Install dependencies.
3. Generate Prisma client.
4. Run migrations.
5. Seed menu data.
6. Start backend server.

```bash
cp .env.example .env
npm install
npm run prisma:generate
npm run prisma:migrate -- --name init
npm run prisma:seed
npm run dev
```

Server URL: `http://localhost:4000`

## Implemented APIs
- `GET /health`
- `GET /api/menu`
- `POST /api/menu`
- `DELETE /api/menu/:id`
- `GET /api/orders?table=Table 1`
- `POST /api/orders`
- `PATCH /api/orders/:id/status`
- `PATCH /api/orders/:id/payment`
- `GET /api/requests`
- `POST /api/requests`
- `PATCH /api/requests/:id/status`
- `DELETE /api/requests/completed`
- `POST /api/auth/otp/send`
- `POST /api/auth/otp/verify`
- `GET /api/auth/session/:table`

## Email notifications for completed + paid orders

When an order reaches:
- `status = served`
- `paymentStatus = paid`

the backend sends a customer email (if `customerEmail` exists on the order) and records it to avoid duplicate sends.

Add SMTP config in `backend/.env`:

```dotenv
SMTP_HOST="smtp.example.com"
SMTP_PORT=587
SMTP_USER="smtp-user"
SMTP_PASS="smtp-password"
EMAIL_FROM="Sunset Cafe <no-reply@sunsetcafe.com>"
```

## Next step
Wire `data.js` storage functions to call these APIs while keeping a fallback local demo mode.

