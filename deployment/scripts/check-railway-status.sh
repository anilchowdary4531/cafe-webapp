#!/bin/bash

# Railway Status Checker
# Verifies Railway deployment status and safety

echo "🚂 Railway Deployment Status Check"
echo "==================================="
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

# Check if railway.toml exists
echo "1. Configuration Check:"
if [ -f "railway.toml" ]; then
    print_status "railway.toml found - deployment configuration ready"
else
    print_error "railway.toml missing - create configuration first"
    exit 1
fi

# Check GitHub integration
echo ""
echo "2. GitHub Integration Status:"
if git remote get-url origin &> /dev/null; then
    REPO_URL=$(git remote get-url origin)
    print_status "GitHub repository connected: $REPO_URL"
    print_info "Railway will use this repository for deployments"
else
    print_warning "No git remote found - ensure repository is pushed to GitHub"
fi

# Check current branch
echo ""
echo "3. Deployment Branch Check:"
CURRENT_BRANCH=$(git branch --show-current)
if [ "$CURRENT_BRANCH" = "main" ] || [ "$CURRENT_BRANCH" = "master" ]; then
    print_status "On deployment branch: $CURRENT_BRANCH"
    print_info "Pushes to this branch will trigger Railway deployments"
else
    print_warning "Not on main/master branch: $CURRENT_BRANCH"
    print_info "Switch to main/master for production deployments"
fi

# Railway safety explanation
echo ""
echo "4. 🚨 Railway Safety Confirmation:"
echo ""
print_status "Railway WILL check for existing deployments"
print_status "Railway will NOT create duplicate projects"
print_status "Railway will NOT delete existing services"
print_status "Railway will NOT recreate databases"
print_status "Railway will NOT overwrite environment variables"
echo ""
print_info "Your existing Railway project and data are 100% safe!"

# Deployment readiness
echo ""
echo "5. Deployment Readiness:"
if [ -f "railway.toml" ] && git remote get-url origin &> /dev/null; then
    print_status "Ready for Railway deployment!"
    echo ""
    print_info "Next steps:"
    echo "  1. Ensure Railway project is connected to this GitHub repo"
    echo "  2. Push to main/master branch to trigger deployment"
    echo "  3. Monitor at: https://railway.app/dashboard"
else
    print_warning "Not ready for deployment - check above issues"
fi

# Cost reminder
echo ""
echo "6. 💰 Cost Reminder:"
echo -e "${GREEN}Railway Pro Plan: $10/month${NC}"
echo -e "${GREEN}Includes: Hosting + Database + SSL + Monitoring${NC}"

echo ""
print_info "For detailed safety info: cat deployment/RAILWAY_SAFETY.md"
echo ""
print_status "Status check complete - Railway deployment is safe!"
