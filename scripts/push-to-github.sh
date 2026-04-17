#!/bin/bash
# push-to-github.sh — Run this AFTER adding your SSH key to GitHub
# See md/LAUNCH_GUIDE.md for instructions

set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"
ROOT="$(cd "$SCRIPT_DIR/.." && pwd)"
GITHUB_USER="${1:-${GITHUB_USER:-anilchowdary4531}}"
MAIN_REPO="${MAIN_REPO:-cafe-webapp}"

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
echo "▶ Publishing play-site to GitHub Pages (same repo)..."
cp -R "$PLAY_SITE_SOURCE/." "$PLAY_SITE_TEMP/"
cd "$PLAY_SITE_TEMP"

git init
git checkout -B gh-pages
git config user.email "anilchowdarya8@gmail.com"
git config user.name "Anil Chowdary"

git add -A
git commit -m "Update Sunset Cafe website"

git remote add origin "git@github.com:$GITHUB_USER/$MAIN_REPO.git"

git push -u origin gh-pages --force
echo "  ✅ https://github.com/$GITHUB_USER/$MAIN_REPO (branch: gh-pages)"

echo ""
echo "======================================================"
echo " ✅ Main repo + GitHub Pages branch pushed!"
echo ""
echo " NOW — Enable GitHub Pages:"
echo "   https://github.com/$GITHUB_USER/$MAIN_REPO/settings/pages"
echo "   → Source: Deploy from a branch → gh-pages → / (root) → Save"
echo ""
echo " Your live URLs (ready in ~60 sec):"
echo "   Website:        https://$GITHUB_USER.github.io/$MAIN_REPO/"
echo "   Privacy Policy: https://$GITHUB_USER.github.io/$MAIN_REPO/privacy-policy.html"
echo ""
echo " NEXT: Deploy backend on Railway"
echo "   See md/LAUNCH_GUIDE.md Step 5"
echo "======================================================"

