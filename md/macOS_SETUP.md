# macOS Complete Installation & Setup Guide

## Prerequisites Check

Before starting, verify your macOS environment:

```bash
# Check macOS version (should be 10.15+)
sw_vers

# Check if Homebrew is installed
brew --version

# If Homebrew not installed, install it:
/bin/bash -c "$(curl -fsSL https://raw.githubusercontent.com/Homebrew/install/HEAD/install.sh)"
```

---

## Step-by-Step Installation

### 1️⃣ Install Node.js & npm

```bash
# Using Homebrew (recommended for macOS)
brew install node@20

# Verify installation
node --version    # Expected: v20.x.x
npm --version     # Expected: 10.x.x

# Update npm to latest
npm install -g npm@latest
```

**Alternative:** Download from https://nodejs.org/ (LTS version)

---

### 2️⃣ Install PostgreSQL

```bash
# Using Homebrew
brew install postgresql@16

# Initialize the database system
initdb /usr/local/var/postgres

# Start PostgreSQL service (runs in background)
brew services start postgresql@16

# Verify installation
psql --version    # Expected: psql (PostgreSQL) 16.x
```

**Verify PostgreSQL is running:**
```bash
brew services list | grep postgresql
# Should show: postgresql@16 started username ~/Library/LaunchAgents/...
```

---

### 3️⃣ Create Database

```bash
# Create cafe_webapp database
createdb cafe_webapp

# Connect to verify (optional)
psql cafe_webapp
# Type: \l (to list databases)
# Type: \q (to exit)
```

**Common Issues:**
```bash
# If "role does not exist" error:
createuser -P $(whoami)  # Create user with password

# If permission denied:
sudo -u postgres createdb cafe_webapp  # Run as postgres user
```

---

### 4️⃣ Clone/Navigate to Project

```bash
# Navigate to your project
cd /Users/anilkumarthammineni/Downloads/cafe-webapp

# Verify structure
ls -la
# Should see: index.html, staff.html, admin.html, backend/, styles.css, etc.
```

---

### 5️⃣ Install Backend Dependencies

```bash
# Enter backend directory
cd backend

# Install npm packages
npm install

# This will install:
# - fastify (web framework)
# - @prisma/client (database client)
# - prisma (database toolkit)
# - typescript (language)
# - tsx (dev runtime)
# - zod (validation)
# - dotenv (environment variables)
# - @fastify/cors (cross-origin support)
```

**Expected output:**
```
added 150 packages in 45s
```

---

### 6️⃣ Setup Environment Variables

```bash
# The .env file should already exist with:
cat .env

# Expected content:
# DATABASE_URL="postgresql://anilkumarthammineni@localhost:5432/cafe_webapp?schema=public"
# PORT=4000
# CORS_ORIGIN="http://localhost:8000"

# ⚠️ If .env is missing or incorrect, update it:
# Replace 'anilkumarthammineni' with your actual macOS username if needed
```

**Find your actual database username:**
```bash
whoami  # Shows your macOS username
```

---

### 7️⃣ Setup Prisma & Database

```bash
# Still in /backend directory

# Generate Prisma client
npm run prisma:generate

# Run migrations (creates tables in database)
npm run prisma:migrate -- --name init

# Seed initial menu data
npm run prisma:seed

# Verify tables were created
psql cafe_webapp -c "\dt"
# Should show: orders, orders_items, customer_sessions, otp_challenges, etc.
```

---

### 8️⃣ Start Backend Server

```bash
# Still in /backend directory

# Start development server
npm run dev

# Expected output:
# [1] 12345
# {"level":30,"time":"2026-04-11T10:00:00.000Z","pid":12345,"msg":"Server listening at http://0.0.0.0:4000"}

# Backend is now running at http://localhost:4000
# Press Ctrl+C to stop
```

**Test if backend is working:**
```bash
# In a new terminal:
curl http://localhost:4000/health

# Expected response:
# {"ok":true}
```

---

### 9️⃣ Setup Frontend Server

```bash
# Open a NEW terminal window/tab

# Navigate to project root
cd /Users/anilkumarthammineni/Downloads/cafe-webapp

# Option A: Using Python (already on macOS)
python3 -m http.server 8000

# Option B: Using npx (if you prefer npm)
npx http-server -p 8000

# Option C: Using global http-server
brew install http-server
http-server -p 8000

# Frontend is now running at http://localhost:8000
# Press Ctrl+C to stop
```

**Expected output:**
```
Serving HTTP on 0.0.0.0 port 8000 (http://0.0.0.0:8000/) ...
```

---

### 🔟 Verify Everything Works

**Test the complete flow:**

```bash
# 1. Open browser
open http://localhost:8000

# 2. Or manually open:
# http://localhost:8000/index.html (Customer)
# http://localhost:8000/staff.html (Staff)
# http://localhost:8000/admin.html (Admin)
```

**Test API from terminal:**
```bash
# Check backend health
curl http://localhost:4000/health

# Get menu (should return default menu)
curl http://localhost:4000/api/menu

# Get orders (should return empty array)
curl http://localhost:4000/api/orders
```

---

## 🎯 Testing the Application

### Customer Flow (index.html)
1. Open http://localhost:8000/index.html
2. Select "Table 1"
3. Click "Start Ordering"
4. Add items to cart (Coffee, Snacks, etc.)
5. Click "Place Order"
6. Enter phone number and click "Send OTP"
7. Enter OTP shown in yellow box
8. Click "Verify & Place Order"
9. ✅ Order should appear in staff dashboard

### Staff Flow (staff.html)
1. Open http://localhost:8000/staff.html
2. Should see orders from step 9 above
3. Click status buttons to change order from "received" → "preparing" → "served"
4. Service requests (Call Waiter, Request Bill) should appear
5. ✅ Updates should reflect in real-time

### Admin Flow (admin.html)
1. Open http://localhost:8000/admin.html
2. Add a new menu item:
   - Name: "Special Chai"
   - Category: "Tea"
   - Price: 2.99
   - Description: "Spiced tea special"
3. Click "Save Item"
4. New item should appear in the list
5. Go to customer page and should see new item in menu
6. ✅ Admin changes are live

---

## 🛠️ Development Mode - Quick Reference

### Two-Terminal Setup
**Terminal 1 - Backend:**
```bash
cd /Users/anilkumarthammineni/Downloads/cafe-webapp/backend
npm run dev
# Watches for changes, auto-restarts
# http://localhost:4000
```

**Terminal 2 - Frontend:**
```bash
cd /Users/anilkumarthammineni/Downloads/cafe-webapp
python3 -m http.server 8000
# http://localhost:8000
```

### Common Development Tasks

```bash
# View database
psql cafe_webapp
\dt                    # List tables
SELECT * FROM "MenuItem";  # View menu items
\q                     # Exit

# Reset database (⚠️ Deletes all data!)
cd backend
npm run prisma:migrate reset
npm run prisma:seed

# Rebuild TypeScript
npm run build          # Creates dist/ folder

# Check TypeScript errors
npx tsc --noEmit       # Check without building

# View logs
# Backend logs appear in Terminal 1
# Open browser Developer Tools (F12) for frontend logs
```

---

## 🚨 Common Issues & Solutions

### ❌ "PostgreSQL connection refused"
```bash
# Check if PostgreSQL is running
brew services list | grep postgres

# Start PostgreSQL
brew services start postgresql@16

# Check PostgreSQL logs
tail -f /usr/local/var/postgresql.log

# Test connection
psql postgres
```

### ❌ "Port 4000 already in use"
```bash
# Find what's using port 4000
lsof -i :4000

# Kill the process
kill -9 <PID>

# Or use different port in .env
# Change PORT=4001 in backend/.env
```

### ❌ "Database does not exist"
```bash
# Create database
createdb cafe_webapp

# Verify
psql cafe_webapp -c "SELECT 1"
```

### ❌ "Permission denied writing to database"
```bash
# Run psql as postgres user
sudo -u postgres psql cafe_webapp
```

### ❌ "CORS error in browser"
Check that CORS_ORIGIN in `.env` matches your frontend URL:
```env
CORS_ORIGIN="http://localhost:8000"
```

### ❌ "Module not found" or npm errors
```bash
# Reinstall everything
rm -rf node_modules package-lock.json
npm install

# Regenerate Prisma client
npm run prisma:generate
```

### ❌ "TypeScript errors but code looks correct"
```bash
# Clear TypeScript cache
npm run prisma:generate
npx tsc --noEmit
```

---

## 📊 Verify Full Setup

Run this checklist to confirm everything is working:

```bash
# 1. Check Node.js
node --version     # Should be v20+

# 2. Check npm
npm --version      # Should be 10+

# 3. Check PostgreSQL is running
brew services list | grep postgres

# 4. Check database exists
psql cafe_webapp -c "SELECT 1"

# 5. Check backend folder has node_modules
ls /Users/anilkumarthammineni/Downloads/cafe-webapp/backend/node_modules | head

# 6. Start backend (in background)
cd /Users/anilkumarthammineni/Downloads/cafe-webapp/backend
npm run dev &

# 7. Test backend API
sleep 2
curl http://localhost:4000/health

# 8. Start frontend (in another background)
cd /Users/anilkumarthammineni/Downloads/cafe-webapp
python3 -m http.server 8000 &

# 9. Test frontend loads
sleep 2
curl -s http://localhost:8000/index.html | head -20

# ✅ If all commands work, you're ready!
```

---

## 🎓 Learning Resources

### Key Concepts to Understand
1. **Node.js/npm** - JavaScript runtime and package manager
2. **PostgreSQL** - Relational database
3. **Prisma** - Database ORM (Object-Relational Mapping)
4. **Fastify** - Web framework for APIs
5. **TypeScript** - Typed superset of JavaScript
6. **REST APIs** - HTTP requests (GET, POST, PATCH, DELETE)

### Official Documentation
- Node.js: https://nodejs.org/docs/
- PostgreSQL: https://www.postgresql.org/docs/
- Prisma: https://www.prisma.io/docs/
- Fastify: https://www.fastify.io/docs/
- TypeScript: https://www.typescriptlang.org/docs/

### Debugging Tips
1. Open Developer Tools in browser: **F12** or **Cmd+Option+I**
2. Check **Network** tab for API calls
3. Check **Console** tab for JavaScript errors
4. Backend logs appear in terminal where `npm run dev` is running
5. Use `console.log()` to debug frontend
6. Use `console.log()` in server.ts to debug backend

---

## 📞 Support Commands

When something goes wrong, gather this info:

```bash
# System info
uname -a
sw_vers

# Node/npm versions
node --version && npm --version

# PostgreSQL status
brew services list
psql --version

# Project dependencies
cd /Users/anilkumarthammineni/Downloads/cafe-webapp/backend
npm list --depth=0

# Database status
psql cafe_webapp -c "\dt"

# Active ports
lsof -i -P -n | grep LISTEN
```

---

## ✅ Success Indicators

You'll know everything is working when:

✅ Backend starts without errors  
✅ `curl http://localhost:4000/health` returns `{"ok":true}`  
✅ Frontend loads at `http://localhost:8000`  
✅ Customer can place an order  
✅ Staff can see the order  
✅ Admin can add menu items  
✅ New admin items appear in customer menu  
✅ Browser console has no red errors  

---

**Next Steps After Setup:**
1. Read the main `TECH_STACK_GUIDE.md`
2. Understand the API endpoints in `backend/README.md`
3. Start building: Wire frontend to backend APIs
4. Add authentication (staff/admin login)
5. Deploy to production

Good luck! 🚀

