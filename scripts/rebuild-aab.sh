#!/bin/bash
# rebuild-aab.sh — Sync Capacitor and rebuild the signed release AAB
# Usage: ./scripts/rebuild-aab.sh https://your-railway-url.up.railway.app
# If no URL given, uses the existing config (localhost)

set -euo pipefail

API_URL="${1:-}"
SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"
ROOT="$(cd "$SCRIPT_DIR/.." && pwd)"
MOBILE="$ROOT/mobile"
AAB="$MOBILE/android/app/build/outputs/bundle/release/app-release.aab"
CONFIG_FILE="$MOBILE/www/config.js"

require_command() {
  command -v "$1" >/dev/null 2>&1 || {
    echo "❌ Required command not found: $1"
    exit 1
  }
}

require_command npm
require_command jarsigner

echo ""
echo "======================================================"
echo " Sunset Cafe — Android Release Builder"
if [ -n "$API_URL" ]; then
  echo " API URL: $API_URL"
else
  echo " API URL: (using current mobile configuration)"
fi
echo "======================================================"
echo ""

JAVA_HOME=$(/usr/libexec/java_home -v 17 2>/dev/null || /usr/libexec/java_home -v 21 2>/dev/null)
export JAVA_HOME
if [ -z "${JAVA_HOME:-}" ]; then
  echo "❌ Could not resolve JAVA_HOME. Install JDK 17 or JDK 21 first."
  exit 1
fi
echo "▶ JAVA_HOME: $JAVA_HOME"

cd "$MOBILE"

echo ""
echo "▶ Step 1: Syncing Capacitor web assets..."
if [ -n "$API_URL" ]; then
  MOBILE_API_BASE_URL="$API_URL" npm run cap:sync
else
  npm run cap:sync
fi

if [ -f "$CONFIG_FILE" ] && grep -q "localhost:4100" "$CONFIG_FILE"; then
  echo "⚠️  Warning: mobile build is still pointing to localhost:4100."
  echo "   For Play Store / production builds, rerun with your live backend URL:"
  echo "   ./scripts/rebuild-aab.sh https://YOUR_RAILWAY_URL.up.railway.app"
fi

echo "  ✅ Sync complete"

echo ""
echo "▶ Step 2: Building release AAB..."
npm run android:bundle:release

if [ ! -f "$AAB" ]; then
  echo "❌ Release AAB was not generated: $AAB"
  exit 1
fi

echo "  ✅ Build complete"

echo ""
echo "▶ Step 3: Verifying signature..."
jarsigner -verify "$AAB" && echo "  ✅ Signature valid" || echo "  ⚠️  Not signed (unsigned AAB)"

echo ""
echo "======================================================"
echo " ✅ AAB ready!"
echo "    $AAB"
SIZE=$(du -sh "$AAB" | cut -f1)
echo "    Size: $SIZE"
echo ""
echo " Upload to Play Console:"
echo "   1. https://play.google.com/console"
echo "   2. Your app → Production → Create release → Upload AAB"
echo "======================================================"

