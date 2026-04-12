# Tools & Dependencies Checklist

## 📋 Complete Tools List

### Required System Tools (Install on Your Mac)

| # | Tool | Version | How to Install | Purpose | Status |
|---|------|---------|-----------------|---------|--------|
| 1 | **Node.js** | 20+ | `brew install node@20` | JavaScript runtime | ⚠️ **MUST INSTALL** |
| 2 | **npm** | 10+ | Comes with Node.js | Package manager | ⚠️ **COMES WITH NODE** |
| 3 | **PostgreSQL** | 14+ | `brew install postgresql@16` | Database | ⚠️ **MUST INSTALL** |
| 4 | **Homebrew** | Latest | See below | macOS package manager | ⚠️ **RECOMMENDED** |
| 5 | **Git** | Latest | `brew install git` | Version control | ✅ OPTIONAL |

---

## 🎯 What to Install RIGHT NOW

### Absolute Minimum (Required)

```bash
# 1. Install Homebrew (if not already)
/bin/bash -c "$(curl -fsSL https://raw.githubusercontent.com/Homebrew/install/HEAD/install.sh)"

# 2. Install Node.js 20 (includes npm)
brew install node@20

# 3. Install PostgreSQL
brew install postgresql@16

# 4. Verify installations
node --version
npm --version
psql --version
```

**Time needed:** ~15 minutes  
**Disk space:** ~800 MB

---

## 📦 Backend Dependencies (Auto-Installed)

These are automatically installed when you run `npm install` in the backend folder:

```json
{
  "dependencies": {
    "fastify": "^5.2.1",           // Web framework
    "@fastify/cors": "^10.0.1",    // CORS support
    "@prisma/client": "^6.6.0",    // Database client
    "dotenv": "^16.4.5",           // Environment variables
    "zod": "^3.24.2"               // Input validation
  },
  "devDependencies": {
    "typescript": "^5.8.3",        // Language compiler
    "prisma": "^6.6.0",            // Database toolkit
    "tsx": "^4.19.3",              // TypeScript runner
    "@types/node": "^22.14.1"      // Node.js types
  }
}
```

**Installation command:**
```bash
cd backend
npm install
```

**Time needed:** ~2 minutes  
**Disk space:** ~200 MB in node_modules

---

## 🧰 Developer Tools (Optional but Recommended)

| Tool | Installation | Purpose | Optional? |
|------|--------------|---------|-----------|
| **Visual Studio Code** | https://code.visualstudio.com/ | Code editor | ✅ Yes |
| **Postman** | `brew install postman` | API testing | ✅ Yes |
| **DBeaver** | `brew install dbeaver-community` | Database GUI | ✅ Yes |
| **TablePlus** | `brew install tableplus` | Database client | ✅ Yes |

---

## 📥 Installation Priority Order

### Phase 1: System Setup (Required - Do First)
```bash
# Step 1: Install Homebrew
/bin/bash -c "$(curl -fsSL https://raw.githubusercontent.com/Homebrew/install/HEAD/install.sh)"

# Step 2: Update Homebrew
brew update

# Step 3: Install Node.js (includes npm)
brew install node@20

# Step 4: Install PostgreSQL
brew install postgresql@16

# Step 5: Verify all work
node --version
npm --version
psql --version
```
**⏱️ Time: 20 minutes**

---

### Phase 2: Project Setup (Required - Do Second)
```bash
# Step 1: Navigate to project
cd /Users/anilkumarthammineni/Downloads/cafe-webapp/backend

# Step 2: Install npm packages
npm install

# Step 3: Create database
createdb cafe_webapp

# Step 4: Setup Prisma
npm run prisma:generate
npm run prisma:migrate -- --name init
npm run prisma:seed
```
**⏱️ Time: 5 minutes**

---

### Phase 3: Development Tools (Optional - Do Anytime)
```bash
# Install VSCode
brew install --cask visual-studio-code

# Install database GUI tool
brew install --cask dbeaver-community

# Install API testing tool  
brew install --cask postman

# Install HTTP server globally (optional)
npm install -g http-server
```
**⏱️ Time: 10 minutes**

---

## ✅ Installation Verification Checklist

Run these commands to verify everything is installed:

```bash
# 1. Check Node.js (should show v20.x.x)
node --version
Expected output: v20.14.0

# 2. Check npm (should show 10.x.x)
npm --version
Expected output: 10.5.0

# 3. Check PostgreSQL (should show version 16)
psql --version
Expected output: psql (PostgreSQL) 16.2

# 4. Check PostgreSQL is running
brew services list | grep postgresql
Expected output: postgresql@16 started ...

# 5. Check database exists
psql cafe_webapp -c "SELECT 1"
Expected output: 
  ?column? 
  ----------
           1

# 6. Check backend folder
ls -la /Users/anilkumarthammineni/Downloads/cafe-webapp/backend/node_modules | head
Expected output: List of 150+ packages

# 7. Check Prisma is ready
ls /Users/anilkumarthammineni/Downloads/cafe-webapp/backend/node_modules/.prisma
Expected output: client directory
```

---

## 🚀 Quick Install Script

Save this as `setup.sh` and run `bash setup.sh`:

```bash
#!/bin/bash
set -e

echo "🔧 Setting up Cafe QR Web App..."

# Check Homebrew
if ! command -v brew &> /dev/null; then
    echo "📦 Installing Homebrew..."
    /bin/bash -c "$(curl -fsSL https://raw.githubusercontent.com/Homebrew/install/HEAD/install.sh)"
fi

# Install Node.js
echo "📦 Installing Node.js..."
brew install node@20

# Install PostgreSQL
echo "📦 Installing PostgreSQL..."
brew install postgresql@16

# Start PostgreSQL
echo "🔄 Starting PostgreSQL..."
brew services start postgresql@16

# Create database
echo "🗄️ Creating database..."
createdb cafe_webapp 2>/dev/null || true

# Install backend dependencies
echo "📦 Installing backend dependencies..."
cd backend
npm install

# Setup Prisma
echo "🗄️ Setting up Prisma..."
npm run prisma:generate
npm run prisma:migrate -- --name init
npm run prisma:seed

echo "✅ Setup complete!"
echo "📝 Next steps:"
echo "  1. Terminal 1: cd backend && npm run dev"
echo "  2. Terminal 2: cd .. && python3 -m http.server 8000"
echo "  3. Open: http://localhost:8000"
```

---

## 🔄 Running the Application

### After installation, to start development:

**Terminal 1 - Backend (Required):**
```bash
cd /Users/anilkumarthammineni/Downloads/cafe-webapp/backend
npm run dev
```

**Terminal 2 - Frontend (Required):**
```bash
cd /Users/anilkumarthammineni/Downloads/cafe-webapp
python3 -m http.server 8000
# or
http-server -p 8000
```

**Open in browser:**
```
http://localhost:8000/index.html    (Customer)
http://localhost:8000/staff.html    (Staff)
http://localhost:8000/admin.html    (Admin)
http://localhost:4000/health        (Backend API health)
```

---

## 📊 Technology Stack Summary

### Frontend
- **Language:** HTML, CSS, JavaScript (Vanilla - No frameworks)
- **Storage:** localStorage (currently) → API calls (next phase)
- **Server:** Python HTTP Server or npm http-server
- **Port:** 8000

### Backend
- **Language:** TypeScript
- **Runtime:** Node.js 20+
- **Framework:** Fastify
- **ORM:** Prisma
- **Database:** PostgreSQL
- **Port:** 4000

### Database
- **Type:** Relational (SQL)
- **Product:** PostgreSQL 16
- **Hosted:** Local development

### Development Tools
- **Package Manager:** npm
- **Compiler:** TypeScript (tsc)
- **Dev Runner:** tsx
- **Validation:** Zod
- **CORS:** @fastify/cors

---

## 🎓 Understanding the Architecture

```
┌─────────────────────────────────────────────────────┐
│                    WEB BROWSER                       │
│  index.html / staff.html / admin.html               │
│          (HTML + CSS + JavaScript)                  │
│                                                     │
│  Running on: http://localhost:8000                  │
│  Server: Python/npm HTTP Server                     │
└─────────────────┬───────────────────────────────────┘
                  │
                  │ HTTP Requests/Responses
                  │ (GET, POST, PATCH, DELETE)
                  │
┌─────────────────┴───────────────────────────────────┐
│              FASTIFY API SERVER                      │
│              (backend/src/server.ts)                │
│                                                     │
│  Running on: http://localhost:4000                  │
│  Language: TypeScript / Node.js 20                  │
│  Framework: Fastify                                │
└─────────────────┬───────────────────────────────────┘
                  │
                  │ SQL Queries
                  │ (CRUD Operations)
                  │
┌─────────────────┴───────────────────────────────────┐
│           POSTGRESQL DATABASE                       │
│          (cafe_webapp database)                     │
│                                                     │
│  Tables: MenuItem, Order, OrderItem, ServiceRequest │
│  Running on: localhost:5432                         │
└─────────────────────────────────────────────────────┘
```

---

## 📋 Command Reference

### Installation Commands
```bash
brew install node@20              # Install Node.js
brew install postgresql@16        # Install PostgreSQL
brew services start postgresql@16 # Start PostgreSQL service
createdb cafe_webapp              # Create database
cd backend && npm install         # Install backend dependencies
npm run prisma:generate           # Generate Prisma client
npm run prisma:migrate -- --name init  # Run migrations
npm run prisma:seed               # Seed demo data
```

### Development Commands
```bash
npm run dev                  # Start backend (with auto-reload)
npm run build                # Compile TypeScript
npm run start                # Run compiled backend
python3 -m http.server 8000  # Start frontend server
```

### Database Commands
```bash
psql cafe_webapp             # Connect to database
\dt                          # List all tables
\q                           # Exit psql
npm run prisma:migrate reset # ⚠️ Reset database (deletes all data)
```

### Debugging Commands
```bash
curl http://localhost:4000/health                # Test backend
curl http://localhost:4000/api/menu              # Get menu
curl http://localhost:8000/index.html            # Test frontend
lsof -i :4000                                    # What's using port 4000
brew services list                               # Check PostgreSQL status
```

---

## 🆘 Emergency Fixes

### Database won't connect
```bash
# Restart PostgreSQL
brew services restart postgresql@16

# Check if running
brew services list | grep postgres

# Try connecting
psql cafe_webapp
```

### Port 4000 in use
```bash
# Find process
lsof -i :4000

# Kill it
kill -9 <PID>
```

### Need to reset everything
```bash
# Stop services
brew services stop postgresql@16

# Reset backend
cd backend
rm -rf node_modules dist .next
npm install

# Reset database
brew services start postgresql@16
dropdb cafe_webapp
createdb cafe_webapp

# Reinitialize
npm run prisma:migrate -- --name init
npm run prisma:seed
npm run dev
```

---

## 📞 Summary of What to Do NOW

1. **Install System Tools:**
   ```bash
   brew install node@20
   brew install postgresql@16
   ```

2. **Setup Database:**
   ```bash
   createdb cafe_webapp
   ```

3. **Install Backend:**
   ```bash
   cd backend
   npm install
   npm run prisma:generate
   npm run prisma:migrate -- --name init
   npm run prisma:seed
   ```

4. **Start Development:**
   ```bash
   # Terminal 1
   npm run dev
   
   # Terminal 2
   python3 -m http.server 8000
   ```

5. **Test in Browser:**
   ```
   http://localhost:8000
   ```

---

**You're all set!** 🎉 Proceed with frontend-to-backend API integration.

