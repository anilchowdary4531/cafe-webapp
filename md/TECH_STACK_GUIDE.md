# Cafe QR-Based Web App - Complete Tech Stack & Setup Guide

**Last Updated:** April 11, 2026  
**Project Status:** MVP Phase → Production Ready Phase

---

## 📋 Executive Summary

This is a **QR-based table ordering system for cafes** with three user roles:
- **Customers**: Order via QR code at table with phone OTP verification
- **Staff**: Manage orders, update status, handle service requests
- **Admin**: Manage menu items, categories, pricing

The project is transitioning from a **frontend-only demo** (localStorage) to a **full-stack application** with PostgreSQL backend.

---

## 🏗️ Current Architecture

### Frontend (Demo Pages - Currently Running)
- **index.html** - Customer ordering page
- **staff.html** - Staff dashboard
- **admin.html** - Admin panel
- **app.js, staff.js, admin.js** - Frontend logic
- **data.js** - localStorage-based data storage (demo only)
- **styles.css** - Unified styling

### Backend (Partially Built - Needs Frontend Integration)
- **Node.js + TypeScript** - Backend API
- **Fastify** - Web framework
- **Prisma** - ORM + migrations
- **PostgreSQL** - Database
- **Zod** - Input validation

### Status
- ✅ Frontend demo complete and working
- ✅ Backend API structure complete
- ⏳ **NEXT STEP**: Wire frontend to call backend APIs

---

## 🛠️ Required Tools & Technologies

### Core Technologies Stack

| Category | Tool | Version | Purpose | Status |
|----------|------|---------|---------|--------|
| **Runtime** | Node.js | 20+ | JavaScript runtime | ✅ Required |
| **Language** | TypeScript | 5.8+ | Type-safe development | ✅ Included |
| **Backend Framework** | Fastify | 5.2+ | Fast web server | ✅ Included |
| **ORM** | Prisma | 6.6+ | Database abstraction | ✅ Included |
| **Database** | PostgreSQL | 14+ | Relational database | ✅ Required |
| **Package Manager** | npm | 10+ | Dependency management | ✅ Required |
| **Dev Runtime** | tsx | 4.19+ | TypeScript runner | ✅ Included |
| **Compiler** | tsc | 5.8+ | TypeScript compiler | ✅ Included |
| **Validation** | Zod | 3.24+ | Schema validation | ✅ Included |
| **CORS** | @fastify/cors | 10.0+ | Cross-origin support | ✅ Included |

---

## 📥 Installation & Setup Instructions

### Phase 1: Local Development Environment Setup

#### Step 1: Install Node.js
```bash
# macOS with Homebrew
brew install node@20

# Verify installation
node --version  # Should show v20.x.x
npm --version   # Should show 10.x.x
```

#### Step 2: Install PostgreSQL
```bash
# macOS with Homebrew
brew install postgresql@16

# Start PostgreSQL service
brew services start postgresql@16

# Verify installation
psql --version
```

#### Step 3: Create Database
```bash
# Create the cafe_webapp database
createdb cafe_webapp

# Connect to verify (optional)
psql cafe_webapp
# Type \q to exit
```

#### Step 4: Backend Setup
```bash
# Navigate to backend directory
cd /Users/anilkumarthammineni/Downloads/cafe-webapp/backend

# Install dependencies
npm install

# Generate Prisma client
npm run prisma:generate

# Create .env file (already exists, verify content)
# Should contain:
# DATABASE_URL="postgresql://anilkumarthammineni@localhost:5432/cafe_webapp?schema=public"
# PORT=4000
# CORS_ORIGIN="http://localhost:8000"

# Run migrations
npm run prisma:migrate -- --name init

# Seed initial menu data
npm run prisma:seed

# Start backend in development mode
npm run dev
# Server runs at http://localhost:4000
```

#### Step 5: Frontend Setup
```bash
# Open new terminal, stay in frontend directory
cd /Users/anilkumarthammineni/Downloads/cafe-webapp

# Install a simple HTTP server (if needed)
npm install -g http-server

# Serve frontend on port 8000
http-server -p 8000

# Or use Python (if available)
python3 -m http.server 8000

# Access at http://localhost:8000
```

---

## 📁 Project Structure

```
cafe-webapp/
├── frontend/
│   ├── index.html          # Customer ordering page
│   ├── staff.html          # Staff dashboard
│   ├── admin.html          # Admin panel
│   ├── app.js              # Customer app logic (616 lines)
│   ├── staff.js            # Staff dashboard logic
│   ├── admin.js            # Admin panel logic
│   ├── data.js             # Storage abstraction (localStorage currently)
│   └── styles.css          # Unified styling
│
└── backend/
    ├── src/
    │   └── server.ts       # Fastify API server (415 lines)
    ├── prisma/
    │   ├── schema.prisma   # Database schema (101 lines)
    │   └── seed.ts         # Initial data seeding
    ├── package.json        # Dependencies
    ├── tsconfig.json       # TypeScript config
    ├── .env                # Environment variables
    └── README.md           # Backend setup guide
```

---

## 🔌 Backend API Endpoints (Ready to Use)

All endpoints are implemented. Frontend needs to call these instead of localStorage.

### Menu Management
```
GET    /api/menu              # Get all menu items
POST   /api/menu              # Create new menu item
DELETE /api/menu/:id          # Delete menu item
```

### Orders
```
GET    /api/orders?table=X    # Get orders for a table
POST   /api/orders            # Create new order
PATCH  /api/orders/:id/status # Update order status (received→preparing→served)
```

### Service Requests (Waiter, Bill, Cigarette Approval)
```
GET    /api/requests                # Get all requests
POST   /api/requests                # Create service request
PATCH  /api/requests/:id/status     # Update request status (pending→completed)
DELETE /api/requests/completed      # Clear completed requests
```

### Phone OTP Authentication
```
POST   /api/auth/otp/send           # Send OTP to phone
POST   /api/auth/otp/verify         # Verify OTP and create session
GET    /api/auth/session/:table     # Check verified session for table
```

---

## 📊 Database Schema Overview

### Key Tables
1. **MenuItem** - Menu items with pricing and category
2. **Order** - Customer orders with items and status
3. **OrderItem** - Individual line items in orders
4. **ServiceRequest** - Waiter calls, bill requests, cigarette approvals
5. **CustomerSession** - Verified phone sessions per table
6. **OtpChallenge** - Active OTP verification attempts

### Enums
- **OrderStatus**: `received` → `preparing` → `served`
- **RequestStatus**: `pending` → `completed`

---

## 🚀 Development Workflow

### Running the Full Stack Locally

**Terminal 1 - Backend Server:**
```bash
cd backend
npm run dev
# Runs on http://localhost:4000
# Auto-reloads on code changes with tsx watch
```

**Terminal 2 - Frontend Server:**
```bash
# From root directory
python3 -m http.server 8000
# Or: http-server -p 8000

# Access http://localhost:8000
```

### Testing the Demo Flow
1. Open `http://localhost:8000/index.html`
2. Select a table → Add items → Place order
3. Verify phone with OTP → See order in Staff Dashboard
4. Open `http://localhost:8000/staff.html` → Update order status
5. Open `http://localhost:8000/admin.html` → Manage menu items

---

## 🔄 Next Steps (Implementation Roadmap)

### Priority 1: Frontend API Integration (Critical)
- [ ] Modify `data.js` to:
  - Detect if backend is available
  - Make API calls instead of localStorage
  - Keep localStorage as fallback for demo mode
- [ ] Update `app.js` to use new data layer
- [ ] Update `staff.js` to use new data layer
- [ ] Update `admin.js` to use new data layer
- [ ] Test all three user flows with backend

### Priority 2: Authentication & Authorization
- [ ] Add login for staff (username/password)
- [ ] Add login for admin (username/password)
- [ ] Implement role-based access control
- [ ] Add password reset functionality
- [ ] Add token-based session management

### Priority 3: Enhancements
- [ ] QR code generation per table
- [ ] Real SMS OTP integration (Twilio, AWS SNS)
- [ ] Payment gateway integration (Stripe, PayPal)
- [ ] Volleyball court ordering system
- [ ] Email notifications to admin
- [ ] SMS order status updates to customers

### Priority 4: DevOps & Deployment
- [ ] Docker containerization
- [ ] CI/CD pipeline (GitHub Actions)
- [ ] Environment configuration (dev/staging/prod)
- [ ] Database backups
- [ ] Monitoring & logging

---

## 📝 Configuration Files

### .env (Backend)
```env
# Database connection
DATABASE_URL="postgresql://anilkumarthammineni@localhost:5432/cafe_webapp?schema=public"

# Server
PORT=4000

# Frontend URL (for CORS)
CORS_ORIGIN="http://localhost:8000"
```

### Important Notes
- Change `anilkumarthammineni` to your actual PostgreSQL username if different
- Update `CORS_ORIGIN` when deploying to production
- Never commit `.env` to version control

---

## ✅ Quick Start Checklist

- [ ] Install Node.js v20+
- [ ] Install PostgreSQL 14+
- [ ] Create `cafe_webapp` database
- [ ] Clone/navigate to project
- [ ] Run `cd backend && npm install`
- [ ] Run `npm run prisma:generate`
- [ ] Run `npm run prisma:migrate -- --name init`
- [ ] Run `npm run prisma:seed`
- [ ] Run `npm run dev` (backend server)
- [ ] In new terminal: `python3 -m http.server 8000` (frontend)
- [ ] Open `http://localhost:8000` in browser
- [ ] Test customer flow
- [ ] Test staff flow
- [ ] Test admin flow

---

## 🆘 Troubleshooting

### Backend won't start
```bash
# Check if port 4000 is in use
lsof -i :4000
# If used, kill the process or use different port

# Check database connection
psql postgresql://anilkumarthammineni@localhost:5432/cafe_webapp
```

### Database migrations fail
```bash
# Reset database (WARNING: Deletes all data)
npm run prisma:migrate reset

# Or manually recreate
dropdb cafe_webapp
createdb cafe_webapp
npm run prisma:migrate -- --name init
```

### CORS errors
- Verify `CORS_ORIGIN` in `.env` matches frontend URL
- For local dev: `http://localhost:8000`
- Check browser console for specific error

### TypeScript errors
```bash
# Regenerate Prisma client
npm run prisma:generate

# Clear node_modules and reinstall
rm -rf node_modules package-lock.json
npm install
```

---

## 📚 Useful Commands

```bash
# Backend - Development
npm run dev              # Start dev server with auto-reload
npm run build            # Compile TypeScript to JavaScript
npm run start            # Run compiled backend

# Backend - Database
npm run prisma:generate  # Generate Prisma client
npm run prisma:migrate   # Create/apply migrations
npm run prisma:seed      # Populate initial data

# Database - Direct
psql cafe_webapp                    # Connect to database
\dt                                 # List all tables
\q                                  # Exit psql

# Kill backend process
kill $(lsof -t -i:4000)
```

---

## 🔗 Important Links & References

- **Node.js Docs**: https://nodejs.org/docs/
- **Fastify Docs**: https://www.fastify.io/docs/
- **Prisma Docs**: https://www.prisma.io/docs/
- **PostgreSQL Docs**: https://www.postgresql.org/docs/
- **TypeScript Docs**: https://www.typescriptlang.org/docs/
- **Zod Validation**: https://zod.dev/

---

## 👥 Development Team Notes

- **Frontend**: Currently localStorage-based, needs migration to API
- **Backend**: Fully implemented, ready for integration
- **Database**: Schema complete, migrations in place
- **Testing**: Manual testing via browsers required
- **Deployment**: Not yet configured

---

## 📄 License & Credits

This is a custom project for Sunset Cafe QR-based ordering system.

