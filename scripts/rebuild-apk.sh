#!/bin/bash
# rebuild-apk.sh -- Sync Capacitor and rebuild the signed release APK
# Usage: ./scripts/rebuild-apk.sh [API_URL]
#
# Examples:
#   ./scripts/rebuild-apk.sh https://api.railway.app
#   ./scripts/rebuild-apk.sh                  # uses MOBILE_API_BASE_URL env var
#
# Environment Variables:
#   MOBILE_API_BASE_URL: Backend API URL (required if not passed as argument)

set -euo pipefail

API_URL="${1:-${MOBILE_API_BASE_URL:-}}"
SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"
ROOT="$(cd "$SCRIPT_DIR/.." && pwd)"
MOBILE="$ROOT/mobile"
APK="$MOBILE/android/app/build/outputs/apk/release/app-release.apk"
AAB="$MOBILE/android/app/build/outputs/bundle/release/app-release.aab"
CONFIG_FILE="$MOBILE/www/config.js"

# Color codes for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

log_info() {
  echo -e "${BLUE}ℹ ${1}${NC}"
}

log_success() {
  echo -e "${GREEN}✓ ${1}${NC}"
}

log_warning() {
  echo -e "${YELLOW}⚠ ${1}${NC}"
}

log_error() {
  echo -e "${RED}✗ ${1}${NC}" >&2
}

require_command() {
  command -v "$1" >/dev/null 2>&1 || {
    log_error "Required command not found: $1"
    exit 1
  }
}

require_command npm
require_command jarsigner

echo ""
log_info "=========================================="
log_info "   Sunset Cafe - Android APK Builder"
log_info "=========================================="
echo ""

# Validate API URL
if [ -z "$API_URL" ]; then
  log_warning "No API URL provided"
  log_info "To specify an API URL, use:"
  log_info "  ./scripts/rebuild-apk.sh https://your-api-url.com"
  log_info "Or set: export MOBILE_API_BASE_URL=https://your-api-url.com"
  log_info ""
  log_info "Using config from: $CONFIG_FILE"
else
  # Check for localhost in production build
  case "$API_URL" in
    *localhost*|*127.0.0.1*)
      log_error "API URL cannot use localhost for release builds: $API_URL"
      exit 1
      ;;
  esac
  log_success "API URL: $API_URL"
fi

# Check Java (Android build supports Java 17+; prefer 21 if installed)
resolve_java_home() {
  /usr/libexec/java_home -v 21 2>/dev/null || /usr/libexec/java_home -v 17 2>/dev/null || true
}

JAVA_HOME=$(resolve_java_home)
export JAVA_HOME
if [ -z "${JAVA_HOME:-}" ]; then
  log_error "Could not resolve JAVA_HOME for JDK 17 or newer"
  log_info "To fix: Install JDK 17+ (https://adoptium.net/)"
  exit 1
fi

JAVA_MAJOR="$($JAVA_HOME/bin/java -version 2>&1 | awk -F '[\".]' '/version/ {print $2}')"
if [ "${JAVA_MAJOR:-0}" -lt 17 ]; then
  log_error "Java version mismatch: found $JAVA_MAJOR, need 17 or newer"
  log_info "Install JDK 17+ and retry"
  exit 1
fi

log_success "Java $JAVA_MAJOR: $JAVA_HOME"

cd "$MOBILE"

echo ""
log_info "Step 1/4: Installing dependencies..."
npm ci --legacy-peer-deps || {
  log_error "Failed to install dependencies"
  exit 1
}
log_success "Dependencies installed"

echo ""
log_info "Step 2/4: Syncing Capacitor web assets..."
if [ -n "$API_URL" ]; then
  MOBILE_API_BASE_URL="$API_URL" npm run cap:sync || {
    log_error "Failed to sync Capacitor"
    exit 1
  }
else
  npm run cap:sync || {
    log_error "Failed to sync Capacitor"
    exit 1
  }
fi
log_success "Capacitor synced"

# Check for localhost in config
if [ -f "$CONFIG_FILE" ] && grep -q "localhost:4100" "$CONFIG_FILE"; then
  log_warning "Config still points to localhost:4100"
  log_info "For production, rerun with your live backend URL:"
  log_info "  ./scripts/rebuild-apk.sh https://YOUR_RAILWAY_URL.up.railway.app"
fi

echo ""
log_info "Step 3/4: Building release APK and AAB..."
npm run android:assemble:release || {
  log_error "Failed to build APK"
  exit 1
}
log_success "Release APK built"

# Verify APK exists
if [ ! -f "$APK" ]; then
  log_error "Release APK not generated: $APK"
  exit 1
fi
log_success "APK verified at: $APK"

echo ""
log_info "Step 4/4: Verifying APK signature..."
if jarsigner -verify "$APK" >/dev/null 2>&1; then
  log_success "Signature valid"
else
  log_warning "APK signature not verified (may be unsigned or debug-signed)"
fi

echo ""
log_info "=========================================="
log_success "APK Build Complete"
log_info "=========================================="
echo ""

SIZE=$(du -sh "$APK" | cut -f1)
log_info "Output: $APK"
log_info "Size: $SIZE"
echo ""

log_info "Installation methods:"
echo "  1. USB (requires adb):"
echo "     adb install -r $APK"
echo ""
echo "  2. Manual (copy to phone and install)"
echo ""

if [ -f "$AAB" ]; then
  AAB_SIZE=$(du -sh "$AAB" | cut -f1)
  log_info "AAB also built: $(basename $AAB) ($AAB_SIZE)"
  log_info "Use AAB for Google Play Store release"
fi

echo ""
log_success "Done!"

