# Cafe QR-Based Web App Demo

This is a simple browser-based MVP for a cafe ordering system.

## What is included
- Customer ordering page (`index.html`)
- Staff dashboard (`staff.html`)
- Admin panel (`admin.html`)
- Menu categories for food, snacks, coffee, tea, juices, ice cream, and restricted cigarette requests
- Local storage-based demo data

## How to run
### Option 1: Open directly
Open `index.html` in your browser.

### Option 2: Use a simple local server
If some browsers block local script loading, run a local server.

Python 3:
```bash
python -m http.server 8000
```
Then open:
```text
http://localhost:8000
```

## Demo flow
1. Open `index.html`
2. Select a table, add items, click `Place Order`, then verify the customer phone number with the demo OTP shown on screen
3. Open `staff.html` to update order status
4. Open `admin.html` to add or remove menu items

## Important note
This is a front-end demo only.
- Data is stored only in the browser
- Customer OTP is simulated in the browser for demo purposes only
- No real SMS login or backend
- No real payment integration
- Cigarette ordering is only a request flow with staff approval

## Next improvements
- Add QR code generation per table
- Add Supabase or Spring Boot backend
- Add login for admin/staff
- Add real payment support
- Add volleyball court ordering

## Backend direction (started)
To move from demo to production, this repo now includes a backend scaffold in `backend/`.

- Backend language: **TypeScript (Node.js)**
- API framework: **Fastify**
- ORM/migrations: **Prisma**
- Build tool: **TypeScript compiler (`tsc`)**
- Dev runner: **`tsx`**
- Database: **PostgreSQL**

Why this stack:
- Fast iteration speed and simple deployment
- Strong typing across API and data layer
- Reliable relational storage for orders, sessions, and admin operations

### Run backend locally
```bash
cd backend
cp .env.example .env
npm install
npm run prisma:generate
npm run prisma:migrate -- --name init
npm run prisma:seed
npm run dev
```

Backend base URL:
```text
http://localhost:4000
```

See backend details in `backend/README.md`.
