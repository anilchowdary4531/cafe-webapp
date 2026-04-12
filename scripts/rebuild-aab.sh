#!/bin/bash
# rebuild-aab.sh — Sync Capacitor and rebuild the signed release AAB
# Usage: ./scripts/rebuild-aab.sh https://your-railway-url.up.railway.app
# If no URL given, uses the existing config (localhost)

set -e

API_URL="${1:-}"
ROOT="/Users/anilkumarthammineni/Downloads/cafe-webapp"
MOBILE="$ROOT/mobile"
AAB="$MOBILE/android/app/build/outputs/bundle/release/app-release.aab"

echo ""
echo "======================================================"
echo " Sunset Cafe — Android Release Builder"
if [ -n "$API_URL" ]; then
  echo " API URL: $API_URL"
else
  echo " API URL: (using existing config — localhost)"
fi
echo "======================================================"
echo ""

export JAVA_HOME=$(/usr/libexec/java_home -v 17 2>/dev/null || /usr/libexec/java_home -v 21 2>/dev/null)
echo "▶ JAVA_HOME: $JAVA_HOME"

cd "$MOBILE"

echo ""
echo "▶ Step 1: Syncing Capacitor web assets..."
if [ -n "$API_URL" ]; then
  MOBILE_API_BASE_URL="$API_URL" npm run cap:sync
else
  npm run cap:sync
fi
echo "  ✅ Sync complete"

echo ""
echo "▶ Step 2: Building release AAB..."
npm run android:bundle:release
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

