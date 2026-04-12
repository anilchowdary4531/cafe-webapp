# ACTION PLAN: Complete Setup & Build Guide

**Your Project:** Cafe QR-Based Table Ordering System  
**Current Status:** Frontend Demo Working + Backend API Ready  
**Next Phase:** Wire Frontend to Backend + Deploy

---

## 📋 EXECUTIVE SUMMARY

### What You Have ✅
- Frontend UI pages (Customer, Staff, Admin) - COMPLETE
- Backend API with Fastify - COMPLETE  
- Database schema (Prisma) - COMPLETE
- Authentication system (OTP) - COMPLETE

### What's Missing ⏳
- Frontend API integration (Frontend calling backend)
- Staff/Admin login system
- Production deployment

### What You Need to Install 🛠️
- **TODAY:** Node.js + PostgreSQL (2 tools, 15 min)
- **THIS WEEK:** VSCode + Postman + DBeaver (3 tools, 20 min)
- **OPTIONAL:** Git, Docker, ESLint, Prettier

---

## 🚀 PHASE 1: SETUP YOUR ENVIRONMENT (Today - 1 Hour)

### Step 1A: Install Node.js
```bash
# Check if already installed
node --version

# If not, install
brew install node@20

# Verify
node --version     # Should show v20.x.x
npm --version      # Should show 10.x.x
```
**⏱️ 5 minutes | ✅ Critical**

---

### Step 1B: Install PostgreSQL
```bash
# Check if already installed
psql --version

# If not, install
brew install postgresql@16

# Start the service
brew services start postgresql@16

# Verify
brew services list | grep postgresql  # Should show "started"
psql postgres -c "SELECT 1"            # Should work
```
**⏱️ 10 minutes | ✅ Critical**

---

### Step 1C: Create Database
```bash
# Create the database
createdb cafe_webapp

# Verify it exists
psql cafe_webapp -c "\dt"

# Note: Empty now, will be populated in Step 2
```
**⏱️ 1 minute | ✅ Critical**

---

### Step 1D: Install Backend Dependencies
```bash
# Navigate to backend
cd /Users/anilkumarthammineni/Downloads/cafe-webapp/backend

# Install npm packages (will install ~150 packages)
npm install

# This installs:
# - fastify (web framework)
# - @prisma/client (database client)
# - typescript (language)
# - tsx (dev runner)
# - zod (validation)
# - and 7 more packages
```
**⏱️ 3 minutes | ✅ Critical**

---

### Step 1E: Setup Prisma
```bash
# Still in /backend directory

# Generate Prisma client
npm run prisma:generate
# Creates: node_modules/.prisma/client

# Create database tables
npm run prisma:migrate -- --name init
# Creates: orders, menu_items, service_requests, etc.

# Seed demo data (13 menu items)
npm run prisma:seed
# Populates: MenuItem table with coffee, food, snacks, etc.
```
**⏱️ 2 minutes | ✅ Critical**

---

### Step 1F: Verify Database Setup
```bash
# Connect to database
psql cafe_webapp

# List tables (should see 6 tables)
\dt
# Output should include:
# - "MenuItem"
# - "Order"  
# - "OrderItem"
# - "ServiceRequest"
# - "CustomerSession"
# - "OtpChallenge"

# Check menu items were seeded
SELECT * FROM "MenuItem";
# Should show 13 items (Cappuccino, Burger, etc.)

# Exit
\q
```
**⏱️ 1 minute | ✅ Verification**

---

### Step 1G: Install Developer Tools (Recommended)
```bash
# VSCode (code editor)
brew install --cask visual-studio-code

# http-server (serve frontend)
npm install -g http-server

# Postman (test APIs)
brew install --cask postman

# DBeaver (view database)
brew install --cask dbeaver-community
```
**⏱️ 20 minutes | ⚠️ Strongly Recommended**

---

**✅ PHASE 1 COMPLETE**  
**Total Time: 45-60 minutes**  
**What works now:** Backend API + Database ready

---

## 🏃 PHASE 2: START THE SERVERS (Today - Quick)

### Terminal 1: Start Backend API
```bash
cd /Users/anilkumarthammineni/Downloads/cafe-webapp/backend
npm run dev

# Expected output:
# [1] 12345
# {"level":30,"time":"2026-04-11T10:30:00.000Z","pid":12345,"msg":"Server listening at http://0.0.0.0:4000"}

# ✅ Backend is running on http://localhost:4000
# Stay in this terminal (don't close it)
```

---

### Terminal 2: Start Frontend Server
```bash
# Open NEW terminal/tab

cd /Users/anilkumarthammineni/Downloads/cafe-webapp

# Option A: Using http-server (recommended if installed)
http-server -p 8000

# Option B: Using Python (default on macOS)
python3 -m http.server 8000

# Expected output:
# Serving HTTP on 0.0.0.0 port 8000 ...

# ✅ Frontend is running on http://localhost:8000
```

---

### Terminal 3: Test Backend (Optional)
```bash
# Open another terminal to test

# Check backend health
curl http://localhost:4000/health
# Expected: {"ok":true}

# Get menu items
curl http://localhost:4000/api/menu
# Expected: [{"id":1,"name":"Veg Sandwich",...}, ...]

# Get orders (empty for now)
curl http://localhost:4000/api/orders
# Expected: []
```

---

**✅ BOTH SERVERS RUNNING**
- Backend: http://localhost:4000 ✅
- Frontend: http://localhost:8000 ✅
- Database: PostgreSQL listening ✅

---

## 🧪 PHASE 3: TEST THE DEMO (Today - 10 minutes)

### Test 1: Customer Flow
```
1. Open browser: http://localhost:8000/index.html
2. Select "Table 1"
3. Click "Start Ordering"
4. Add items to cart (Coffee, Snacks)
5. Click "Place Order"
6. Enter phone: 9876543210
7. Click "Send OTP"
8. Copy OTP from yellow box
9. Paste into OTP field
10. Click "Verify & Place Order"
✅ Order placed successfully
```

---

### Test 2: Staff Dashboard
```
1. Open: http://localhost:8000/staff.html
2. Should see your order from Test 1
3. Click order status button
4. Change from "received" → "preparing" → "served"
5. Go back to customer page
6. Click "My Orders"
7. Should see updated status
✅ Status updates working
```

---

### Test 3: Admin Panel
```
1. Open: http://localhost:8000/admin.html
2. Add new item:
   - Name: "Test Coffee"
   - Category: "Coffee"
   - Price: 3.99
   - Description: "Test item"
3. Click "Save Item"
4. Go to customer page
5. Refresh and look in Coffee category
6. Should see "Test Coffee"
✅ Admin changes live
```

---

**✅ PHASE 3 COMPLETE - MVP IS WORKING!**

---

## 📊 PHASE 4: INTEGRATE FRONTEND WITH BACKEND (Next Week)

**Current State:** Frontend uses localStorage, Backend API is separate  
**Target:** Frontend calls Backend APIs

### Files to Modify

#### 1. `data.js` - Create API Wrapper
```javascript
// Current: Uses localStorage only
// Target: Use API if available, fallback to localStorage

const API_URL = 'http://localhost:4000';

async function loadMenu() {
  try {
    const response = await fetch(`${API_URL}/api/menu`);
    return await response.json();
  } catch {
    // Fallback to localStorage for demo
    return loadMenuFromStorage();
  }
}

// Do same for:
// - loadOrders/saveOrders
// - loadRequests/saveRequests  
// - loadCustomerSessions/saveCustomerSessions
// - loadOtpChallenges/saveOtpChallenges
```

#### 2. `app.js` - Update Order Creation
```javascript
// Current: saveOrders(orders) directly
// Target: POST to /api/orders endpoint

async function finalizeOrder() {
  const payload = {
    table: activeTable,
    note: orderNote.value,
    customerPhone: customerSession.phone,
    customerPhoneMasked: maskPhoneNumber(customerSession.phone),
    items: cart.map(item => ({...}))
  };
  
  const response = await fetch('http://localhost:4000/api/orders', {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(payload)
  });
  
  if (response.ok) {
    alert('Order placed');
    cart = [];
  }
}
```

#### 3. `staff.js` - Update Status Changes
```javascript
// Current: Updates local storage
// Target: PATCH /api/orders/:id/status

async function updateOrderStatus(orderId, newStatus) {
  const response = await fetch(
    `http://localhost:4000/api/orders/${orderId}/status`,
    {
      method: 'PATCH',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ status: newStatus })
    }
  );
  
  if (response.ok) {
    console.log('Status updated');
    loadOrders(); // Refresh list
  }
}
```

#### 4. `admin.js` - Update Menu Management
```javascript
// Current: Modifies localStorage
// Target: POST/DELETE to /api/menu endpoints

async function addMenuItem(item) {
  const response = await fetch('http://localhost:4000/api/menu', {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(item)
  });
  
  if (response.ok) {
    const created = await response.json();
    console.log('Item added:', created);
    loadMenu(); // Refresh
  }
}

async function deleteMenuItem(itemId) {
  const response = await fetch(`http://localhost:4000/api/menu/${itemId}`, {
    method: 'DELETE'
  });
  
  if (response.ok) {
    console.log('Item deleted');
    loadMenu(); // Refresh
  }
}
```

---

## 🔐 PHASE 5: ADD AUTHENTICATION (Week 2)

### Backend Changes Needed
1. Add login endpoint (`POST /api/auth/login`)
2. Add password hashing (bcryptjs)
3. Add JWT tokens
4. Add middleware to check auth
5. Add staff and admin roles

### Frontend Changes Needed
1. Create login page (staff.html, admin.html)
2. Store auth token
3. Send token in API headers
4. Redirect to login if token invalid

---

## 🚀 PHASE 6: PREPARE FOR PRODUCTION (Week 3)

### Backend
1. Add error handling
2. Add request logging
3. Add rate limiting
4. Add input validation (already done with Zod)
5. Optimize database queries

### Frontend
1. Build process for assets
2. Minify CSS/JS
3. Add error boundaries
4. Add loading states
5. Add error messages

### Database
1. Add backups
2. Add indexes for performance
3. Add data validation

### Deployment
1. Setup Docker
2. Deploy to hosting (Railway, Heroku, DigitalOcean)
3. Setup CI/CD with GitHub Actions
4. Setup monitoring

---

## 📚 DOCUMENTATION CREATED FOR YOU

| Document | Purpose | When to Read |
|----------|---------|--------------|
| **TECH_STACK_GUIDE.md** | Overview of entire tech stack | Now |
| **macOS_SETUP.md** | Step-by-step installation guide | Now (if installing) |
| **TOOLS_CHECKLIST.md** | All tools with installation info | Now |
| **TOOLS_ANALYSIS.md** | Detailed analysis of each tool | When deciding what to install |
| **ACTION_PLAN.md** (this file) | Everything in order | Now |

---

## ✅ TODAY'S TODO LIST

### Before Lunch (30 min)
- [ ] Run: `brew install node@20`
- [ ] Run: `brew install postgresql@16`
- [ ] Run: `createdb cafe_webapp`
- [ ] Run: `cd backend && npm install`

### After Lunch (15 min)
- [ ] Run: `npm run prisma:generate`
- [ ] Run: `npm run prisma:migrate -- --name init`
- [ ] Run: `npm run prisma:seed`

### Mid-Afternoon (20 min) - Optional but Recommended
- [ ] Run: `brew install --cask visual-studio-code`
- [ ] Run: `npm install -g http-server`
- [ ] Run: `brew install --cask postman`
- [ ] Run: `brew install --cask dbeaver-community`

### Evening (Start servers - 10 min)
- [ ] Terminal 1: `cd backend && npm run dev`
- [ ] Terminal 2: `cd ..` then `http-server -p 8000`
- [ ] Open: `http://localhost:8000`
- [ ] Test customer flow

### Tomorrow Morning (Optional)
- [ ] Review TECH_STACK_GUIDE.md
- [ ] Read backend/README.md
- [ ] Plan frontend API integration

---

## 🎯 SUCCESS CRITERIA

You'll know PHASE 1 is complete when:

✅ `node --version` shows v20.x.x  
✅ `npm --version` shows 10.x.x  
✅ `psql cafe_webapp -c "SELECT 1"` works  
✅ `cd backend && npm run dev` starts without errors  
✅ `curl http://localhost:4000/health` returns `{"ok":true}`  
✅ `http-server -p 8000` serves frontend  
✅ `http://localhost:8000/index.html` loads in browser  
✅ Customer can place an order  
✅ Staff can see and update order  
✅ Admin can add menu items  

---

## 🆘 IF SOMETHING GOES WRONG

### Problem: "PostgreSQL connection refused"
```bash
brew services restart postgresql@16
psql cafe_webapp -c "SELECT 1"
```

### Problem: "npm install fails"
```bash
rm -rf node_modules package-lock.json
npm install
```

### Problem: "Port 4000 already in use"
```bash
lsof -i :4000
kill -9 <PID>
npm run dev
```

### Problem: "Database doesn't exist"
```bash
createdb cafe_webapp
npm run prisma:migrate -- --name init
```

### Problem: "Backend won't start"
```bash
npm run prisma:generate
npm run dev
```

**Still stuck?** Check TOOLS_CHECKLIST.md troubleshooting section

---

## 📞 QUICK REFERENCE COMMANDS

```bash
# Check installations
node --version
npm --version
psql --version

# Manage PostgreSQL
brew services start postgresql@16
brew services stop postgresql@16
brew services list | grep postgresql

# Backend management
cd backend && npm run dev      # Start dev server
npm run build                  # Compile TypeScript
npm run prisma:generate        # Regenerate Prisma client
npm run prisma:migrate -- --name init  # Create tables

# Database management
psql cafe_webapp               # Connect to database
\dt                            # List tables
\q                             # Exit
npm run prisma:migrate reset   # ⚠️ Reset database

# Frontend server
http-server -p 8000           # Serve frontend
python3 -m http.server 8000   # Alternative

# Testing APIs
curl http://localhost:4000/health
curl http://localhost:4000/api/menu
curl -X POST http://localhost:4000/api/auth/otp/send \
  -H "Content-Type: application/json" \
  -d '{"table":"Table 1","phone":"9876543210"}'
```

---

## 🎓 KEY LEARNINGS

### System Architecture
- **Frontend:** HTML/CSS/JS served on port 8000
- **Backend:** Fastify API on port 4000
- **Database:** PostgreSQL with Prisma ORM
- **Communication:** HTTP requests (REST API)

### Development Flow
1. Write/edit code
2. Backend auto-reloads with `tsx watch`
3. Frontend auto-reloads when you refresh browser
4. Test with Postman or curl
5. View database with DBeaver

### How Data Flows
```
Browser Form (index.html)
    ↓
JavaScript (app.js)
    ↓
Fetch to API (http://localhost:4000/api/orders)
    ↓
Fastify Route Handler (server.ts)
    ↓
Prisma ORM (schema.prisma)
    ↓
PostgreSQL Database
```

---

## 🚀 NEXT STEPS AFTER TODAY

### Next: Frontend API Integration (1-2 days)
- Modify `data.js` to call backend APIs
- Test each endpoint (menu, orders, auth)
- Ensure frontend/backend data stays in sync

### Then: Authentication (2-3 days)
- Add staff login system
- Add admin login system  
- Protect endpoints with auth checks
- Add role-based access control

### Then: Deployment (3-5 days)
- Setup Docker for backend
- Deploy to Railway or Heroku
- Setup GitHub Actions CI/CD
- Go live!

---

## 📋 FINAL CHECKLIST

- [ ] You have completed PHASE 1 (Install tools + database)
- [ ] You have completed PHASE 2 (Servers running)
- [ ] You have completed PHASE 3 (Demo working)
- [ ] You have read TECH_STACK_GUIDE.md
- [ ] You understand the architecture
- [ ] You know what to install
- [ ] You know how to run the app
- [ ] You're ready to build!

---

**CONGRATULATIONS!** 🎉  

You now have a complete working MVP of the Cafe QR Ordering System!

Next: Start frontend API integration → Add authentication → Deploy to production

**Start Date:** April 11, 2026  
**Estimated MVP Complete:** April 25, 2026  
**Estimated Production Ready:** May 9, 2026

