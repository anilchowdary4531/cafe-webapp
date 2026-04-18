#!/bin/bash

# Railway App Availability Diagnostic
# Comprehensive check of why the app is unavailable

echo "🔍 Railway App Availability Diagnostic"
echo "======================================"
echo ""

# Colors
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
RED='\033[0;31m'
BLUE='\033[0;34m'
NC='\033[0m'

print_status() {
    echo -e "${GREEN}✓${NC} $1"
}

print_warning() {
    echo -e "${YELLOW}⚠${NC} $1"
}

print_error() {
    echo -e "${RED}✗${NC} $1"
}

print_info() {
    echo -e "${BLUE}ℹ${NC} $1"
}

print_step() {
    echo -e "${BLUE}Step $1:${NC} $2"
}

# Step 1: Check Railway CLI
echo "Step 1: Checking Railway CLI Installation"
echo "----------------------------------------"
if command -v railway &> /dev/null; then
    print_status "Railway CLI is installed"
    RAILWAY_INSTALLED=true
else
    print_warning "Railway CLI not found - will use web interface"
    RAILWAY_INSTALLED=false
fi

# Step 2: Check Railway Account
echo ""
print_step "2" "Checking Railway Account Access"
if [ "$RAILWAY_INSTALLED" = true ] && [ -n "$RAILWAY_TOKEN" ]; then
    print_status "Railway token found - can authenticate"
    CAN_AUTH=true
else
    print_warning "Railway token not set - manual setup required"
    CAN_AUTH=false
fi

# Step 3: Check GitHub Repository
echo ""
print_step "3" "Checking GitHub Repository Status"
if git remote get-url origin &> /dev/null; then
    REPO_URL=$(git remote get-url origin)
    print_status "GitHub repository: $REPO_URL"

    # Check if it's public
    if curl -s "https://api.github.com/repos/${REPO_URL#*github.com/}" | grep -q '"private":true'; then
        print_warning "Repository is private - Railway needs access"
    else
        print_status "Repository is public - Railway can access"
    fi
else
    print_error "No GitHub remote found"
fi

# Step 4: Check Railway Project
echo ""
print_step "4" "Checking Railway Project Status"
if [ "$CAN_AUTH" = true ]; then
    echo "Attempting to check Railway projects..."
    if railway projects list &> /dev/null; then
        PROJECT_COUNT=$(railway projects list | wc -l)
        if [ "$PROJECT_COUNT" -gt 0 ]; then
            print_status "Railway projects found: $PROJECT_COUNT"
            railway projects list
        else
            print_warning "No Railway projects found"
        fi
    else
        print_error "Cannot access Railway projects"
    fi
else
    print_warning "Cannot check Railway projects without authentication"
fi

# Step 5: Check railway.toml
echo ""
print_step "5" "Validating railway.toml Configuration"
if [ -f "railway.toml" ]; then
    print_status "railway.toml found"

    # Check for required services
    if grep -q "\[cafe-backend\]" railway.toml; then
        print_status "Node.js backend service configured"
    else
        print_error "Node.js backend service not found in railway.toml"
    fi

    if grep -q "\[cafe-spring-backend\]" railway.toml; then
        print_status "Spring backend service configured"
    else
        print_error "Spring backend service not found in railway.toml"
    fi

    # Check for database
    if grep -q "\[database\]" railway.toml; then
        print_status "Database configuration found"
    else
        print_warning "Database configuration not found"
    fi
else
    print_error "railway.toml not found in project root"
fi

# Step 6: Check Environment Variables
echo ""
print_step "6" "Checking Required Environment Variables"
REQUIRED_VARS=("JWT_SECRET" "EMAIL_USER" "EMAIL_PASS" "TWILIO_ACCOUNT_SID" "TWILIO_AUTH_TOKEN" "TWILIO_PHONE_NUMBER")
MISSING_VARS=()

for var in "${REQUIRED_VARS[@]}"; do
    if grep -q "$var" railway.toml; then
        print_status "$var configured in railway.toml"
    else
        MISSING_VARS+=("$var")
        print_warning "$var not found in railway.toml"
    fi
done

# Step 7: Local Testing
echo ""
print_step "7" "Local Development Environment Check"
if [ -d "backend" ] && [ -f "backend/package.json" ]; then
    print_status "Node.js backend directory found"
else
    print_error "Node.js backend not found"
fi

if [ -d "backend-spring" ] && [ -f "backend-spring/pom.xml" ]; then
    print_status "Spring backend directory found"
else
    print_error "Spring backend not found"
fi

# Step 8: Build Test
echo ""
print_step "8" "Testing Local Builds"
echo "Testing Node.js backend build..."
if cd backend && npm run build &> /dev/null; then
    print_status "Node.js backend builds successfully"
else
    print_error "Node.js backend build failed"
fi

echo "Testing Spring backend build..."
if cd ../backend-spring && mvn clean package -DskipTests -q; then
    print_status "Spring backend builds successfully"
else
    print_error "Spring backend build failed"
fi

cd ../..

# Step 9: Diagnosis and Recommendations
echo ""
echo "🔍 DIAGNOSIS & RECOMMENDATIONS"
echo "=============================="

if [ ! -f "railway.toml" ]; then
    print_error "CRITICAL: railway.toml missing - Railway cannot deploy"
    echo "Solution: Ensure railway.toml exists in project root"
elif [ "$CAN_AUTH" = false ]; then
    print_warning "Railway not authenticated - manual setup required"
    echo "Solution: Set up Railway project manually via web interface"
elif ! git remote get-url origin &> /dev/null; then
    print_error "No GitHub repository - Railway cannot connect"
    echo "Solution: Push code to GitHub first"
else
    print_info "✅ All prerequisites met - ready for Railway deployment!"
    echo "Next: Set up Railway project and push to trigger deployment"
fi

echo ""
print_info "🚀 IMMEDIATE ACTION ITEMS:"
echo ""
echo "1. 🌐 Go to https://railway.app"
echo "2. 🔗 Connect your GitHub repository: $REPO_URL"
echo "3. ⚙️ Railway will auto-detect services from railway.toml"
echo "4. 🔑 Set environment variables in Railway dashboard:"
for var in "${MISSING_VARS[@]}"; do
    echo "   - $var"
done
echo "5. 🚀 Push to main branch to trigger deployment"
echo "6. 📊 Monitor at: https://railway.app/dashboard"

echo ""
print_info "🔧 If still unavailable after setup:"
echo "   - Check Railway deployment logs"
echo "   - Verify environment variables are set"
echo "   - Test health check endpoints: /health and /actuator/health"
echo "   - Check database connection"

echo ""
print_status "Diagnostic complete - check the action items above!"
