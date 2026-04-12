#!/bin/bash
# push-to-github.sh — Run this AFTER adding your SSH key to GitHub
# See LAUNCH_GUIDE.md Step 2 for instructions

set -e

ROOT="/Users/anilkumarthammineni/Downloads/cafe-webapp"
GITHUB_USER="anilchowdary4531"

echo ""
echo "======================================================"
echo " Sunset Cafe — Push to GitHub"
echo "======================================================"

# ── 1. Push main app repo ─────────────────────────────────────────────────────
echo ""
echo "▶ Pushing cafe-webapp to GitHub..."
cd "$ROOT"
git push -u origin main
echo "  ✅ https://github.com/$GITHUB_USER/cafe-webapp"

# ── 2. Push play-site (website + privacy policy) ─────────────────────────────
echo ""
echo "▶ Publishing sunset-cafe-site to GitHub Pages..."
cd "$ROOT/play-site"

if [ ! -d ".git" ]; then
  git init
  git checkout -b main
  git config user.email "anilchowdarya8@gmail.com"
  git config user.name "Anil Chowdary"
fi

git add -A
git diff --cached --quiet && echo "  ℹ️  No changes" || git commit -m "Update Sunset Cafe website"
git remote remove origin 2>/dev/null || true
git remote add origin "git@github.com:$GITHUB_USER/sunset-cafe-site.git"
git push -u origin main --force
echo "  ✅ https://github.com/$GITHUB_USER/sunset-cafe-site"

echo ""
echo "======================================================"
echo " ✅ Both repos pushed!"
echo ""
echo " NOW — Enable GitHub Pages:"
echo "   https://github.com/$GITHUB_USER/sunset-cafe-site/settings/pages"
echo "   → Source: main branch → / (root) → Save"
echo ""
echo " Your live URLs (ready in ~60 sec):"
echo "   Website:        https://$GITHUB_USER.github.io/sunset-cafe-site/"
echo "   Privacy Policy: https://$GITHUB_USER.github.io/sunset-cafe-site/privacy-policy.html"
echo ""
echo " NEXT: Deploy backend on Railway"
echo "   See LAUNCH_GUIDE.md Step 5"
echo "======================================================"

