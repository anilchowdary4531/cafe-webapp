#!/bin/bash

# Cafe Webapp Cost Calculator
# Estimates deployment costs and potential savings

echo "💰 Cafe Webapp Cost Calculator"
echo "================================"
echo ""

# Colors
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
RED='\033[0;31m'
BLUE='\033[0;34m'
NC='\033[0m'

# Function to format currency
format_currency() {
    printf "$%.2f" "$1"
}

# Function to calculate percentage savings
calculate_savings() {
    local old_cost=$1
    local new_cost=$2
    local savings=$((old_cost - new_cost))
    local percentage=$((savings * 100 / old_cost))
    echo "$savings|$percentage"
}

# Traditional approach costs (estimated)
echo "📊 Traditional Multi-Platform Deployment Costs"
echo "---------------------------------------------"

# Web hosting (AWS/Google Cloud/Azure)
WEB_HOSTING=500
echo "Web Application Hosting: $(format_currency $WEB_HOSTING)/month"

# Mobile development and deployment
MOBILE_DEV=300  # App store fees, CI/CD
echo "Mobile Apps (Android/iOS): $(format_currency $MOBILE_DEV)/month"

# Desktop application
DESKTOP_BUILD=200  # Build servers, distribution
echo "Desktop Application: $(format_currency $DESKTOP_BUILD)/month"

# Development overhead
DEV_OVERHEAD=400  # Maintaining 3 separate codebases
echo "Development Overhead: $(format_currency $DEV_OVERHEAD)/month"

TRADITIONAL_TOTAL=$((WEB_HOSTING + MOBILE_DEV + DESKTOP_BUILD + DEV_OVERHEAD))
echo ""
echo -e "${RED}Traditional Total: $(format_currency $TRADITIONAL_TOTAL)/month${NC}"
echo ""

# Unified approach costs
echo "🚀 Unified Deployment Costs (This Project)"
echo "-----------------------------------------"

# Single CI/CD pipeline
CI_CD=150  # GitHub Actions
echo "CI/CD Pipeline: $(format_currency $CI_CD)/month"

# Multi-platform hosting
MULTI_HOSTING=350  # Railway or optimized cloud
echo "Multi-Platform Hosting: $(format_currency $MULTI_HOSTING)/month"

# Development savings
DEV_SAVINGS=200  # Single codebase maintenance
echo "Development Savings: -$(format_currency $DEV_SAVINGS)/month"

UNIFIED_TOTAL=$((CI_CD + MULTI_HOSTING - DEV_SAVINGS))
echo ""
echo -e "${GREEN}Unified Total: $(format_currency $UNIFIED_TOTAL)/month${NC}"
echo ""

# Calculate savings
SAVINGS_DATA=$(calculate_savings $TRADITIONAL_TOTAL $UNIFIED_TOTAL)
MONTHLY_SAVINGS=$(echo $SAVINGS_DATA | cut -d'|' -f1)
PERCENTAGE=$(echo $SAVINGS_DATA | cut -d'|' -f2)

echo "💸 Cost Savings Analysis"
echo "-----------------------"
echo -e "${GREEN}Monthly Savings: $(format_currency $MONTHLY_SAVINGS)${NC}"
echo -e "${GREEN}Percentage Saved: ${PERCENTAGE}%${NC}"

# Annual projection
ANNUAL_SAVINGS=$((MONTHLY_SAVINGS * 12))
echo -e "${GREEN}Annual Savings: $(format_currency $ANNUAL_SAVINGS)${NC}"
echo ""

# Break-even analysis
if [ $MONTHLY_SAVINGS -gt 0 ]; then
    echo "📈 Break-Even Analysis"
    echo "---------------------"
    INITIAL_SETUP=500  # Estimated one-time setup cost
    MONTHS_TO_BREAK_EVEN=$((INITIAL_SETUP / MONTHLY_SAVINGS + 1))
    echo "One-time Setup Cost: $(format_currency $INITIAL_SETUP)"
    echo "Break-even Period: $MONTHS_TO_BREAK_EVEN months"
    echo ""
fi

# Recommendations
echo "💡 Recommendations"
echo "-----------------"
if [ $PERCENTAGE -ge 30 ]; then
    echo -e "${GREEN}✓ Excellent savings potential!${NC}"
    echo "  - Implement unified deployment immediately"
    echo "  - Focus on optimizing resource usage"
elif [ $PERCENTAGE -ge 20 ]; then
    echo -e "${YELLOW}✓ Good savings potential${NC}"
    echo "  - Consider gradual migration to unified approach"
    echo "  - Start with web deployment optimization"
else
    echo -e "${BLUE}✓ Moderate savings${NC}"
    echo "  - Evaluate specific cost centers"
    echo "  - Focus on development efficiency"
fi

echo ""
echo "📝 Notes:"
echo "  - Costs are estimates based on typical small-medium business usage"
echo "  - Actual savings depend on your specific usage patterns"
echo "  - Additional savings possible with auto-scaling and optimization"
echo "  - Cloud provider discounts and reserved instances not included"

echo ""
echo "🔧 To optimize further:"
echo "  - Monitor actual resource usage"
echo "  - Adjust auto-scaling parameters"
echo "  - Use spot/preemptible instances where possible"
echo "  - Implement cost allocation tags"
