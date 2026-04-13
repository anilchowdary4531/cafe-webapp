#!/bin/bash
# push-to-github.sh — Run this AFTER adding your SSH key to GitHub
# See md/LAUNCH_GUIDE.md for instructions

set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"
ROOT="$(cd "$SCRIPT_DIR/.." && pwd)"
GITHUB_USER="${1:-${GITHUB_USER:-anilchowdary4531}}"
MAIN_REPO="${MAIN_REPO:-cafe-webapp}"
SITE_REPO="${SITE_REPO:-sunset-cafe-site}"

require_command() {
  command -v "$1" >/dev/null 2>&1 || {
    echo "❌ Required command not found: $1"
    exit 1
  }
}

require_command git

PLAY_SITE_SOURCE="$ROOT/play-site"
PLAY_SITE_TEMP="$(mktemp -d)"

cleanup() {
  rm -rf "$PLAY_SITE_TEMP"
}

trap cleanup EXIT

echo ""
echo "======================================================"
echo " Sunset Cafe — Push to GitHub"
echo " GitHub Username: $GITHUB_USER"
echo "======================================================"

# ── 1. Push main app repo ─────────────────────────────────────────────────────
echo ""
echo "▶ Pushing cafe-webapp to GitHub..."
cd "$ROOT"

if [ ! -d .git ]; then
  echo "❌ Git is not initialized in $ROOT"
  exit 1
fi

if git remote get-url origin >/dev/null 2>&1; then
  git remote set-url origin "git@github.com:$GITHUB_USER/$MAIN_REPO.git"
else
  git remote add origin "git@github.com:$GITHUB_USER/$MAIN_REPO.git"
fi

git push -u origin main
echo "  ✅ https://github.com/$GITHUB_USER/$MAIN_REPO"

# ── 2. Push play-site (website + privacy policy) ─────────────────────────────
echo ""
echo "▶ Publishing sunset-cafe-site to GitHub Pages..."
cp -R "$PLAY_SITE_SOURCE/." "$PLAY_SITE_TEMP/"
cd "$PLAY_SITE_TEMP"

git init
git checkout -B main
git config user.email "anilchowdarya8@gmail.com"
git config user.name "Anil Chowdary"

git add -A
git commit -m "Update Sunset Cafe website"

if git remote get-url origin >/dev/null 2>&1; then
  git remote set-url origin "git@github.com:$GITHUB_USER/$SITE_REPO.git"
else
  git remote add origin "git@github.com:$GITHUB_USER/$SITE_REPO.git"
fi

git push -u origin main --force
echo "  ✅ https://github.com/$GITHUB_USER/$SITE_REPO"

echo ""
echo "======================================================"
echo " ✅ Both repos pushed!"
echo ""
echo " NOW — Enable GitHub Pages:"
echo "   https://github.com/$GITHUB_USER/$SITE_REPO/settings/pages"
echo "   → Source: main branch → / (root) → Save"
echo ""
echo " Your live URLs (ready in ~60 sec):"
echo "   Website:        https://$GITHUB_USER.github.io/$SITE_REPO/"
echo "   Privacy Policy: https://$GITHUB_USER.github.io/$SITE_REPO/privacy-policy.html"
echo ""
echo " NEXT: Deploy backend on Railway"
echo "   See md/LAUNCH_GUIDE.md Step 5"
echo "======================================================"

