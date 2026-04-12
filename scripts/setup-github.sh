#!/bin/bash
# setup-github.sh — Initialize git, push main app to GitHub, deploy play-site to GitHub Pages
# Usage: ./setup-github.sh YOUR_GITHUB_USERNAME
# Example: ./setup-github.sh anilchowdary4531

set -e

GITHUB_USERNAME="${1:-anilchowdary4531}"
MAIN_REPO="cafe-webapp"
SITE_REPO="sunset-cafe-site"

ROOT="/Users/anilkumarthammineni/Downloads/cafe-webapp"

echo ""
echo "======================================================"
echo " Sunset Cafe — GitHub Setup Script"
echo " GitHub Username: $GITHUB_USERNAME"
echo "======================================================"
echo ""

# ── 1. Initialize main repo ──────────────────────────────────────────────────
echo "▶ Step 1: Initializing main git repository..."
cd "$ROOT"

if [ ! -d ".git" ]; then
  git init
  git checkout -b main
  echo "  ✅ Git initialized"
else
  echo "  ℹ️  Git already initialized"
fi

git add -A
git commit -m "Initial commit: Sunset Cafe full stack app" 2>/dev/null || \
  git commit --allow-empty -m "Initial commit: Sunset Cafe full stack app"

echo "  ✅ Changes committed"

echo ""
echo "▶ Step 2: Create GitHub repos (do this manually if not done):"
echo "   → https://github.com/new"
echo "   Repo 1: $MAIN_REPO (private ok)"
echo "   Repo 2: $SITE_REPO (must be PUBLIC for GitHub Pages)"
echo ""

read -p "   Have you created both repos? (y/n): " created
if [ "$created" != "y" ]; then
  echo "   Create them first at https://github.com/new then re-run this script."
  exit 1
fi

# ── 2. Push main repo ────────────────────────────────────────────────────────
echo ""
echo "▶ Step 3: Pushing main repo to GitHub..."
git remote remove origin 2>/dev/null || true
git remote add origin "https://github.com/$GITHUB_USERNAME/$MAIN_REPO.git"
git push -u origin main
echo "  ✅ Main repo pushed: https://github.com/$GITHUB_USERNAME/$MAIN_REPO"

# ── 3. Push play-site to separate repo ──────────────────────────────────────
echo ""
echo "▶ Step 4: Publishing play-site to GitHub Pages..."
cd "$ROOT/play-site"

if [ ! -d ".git" ]; then
  git init
  git checkout -b main
fi

git add -A
git commit -m "Add Sunset Cafe website and privacy policy" 2>/dev/null || \
  git commit --allow-empty -m "Update site"

git remote remove origin 2>/dev/null || true
git remote add origin "https://github.com/$GITHUB_USERNAME/$SITE_REPO.git"
git push -u origin main --force
echo "  ✅ Site pushed: https://github.com/$GITHUB_USERNAME/$SITE_REPO"

echo ""
echo "======================================================"
echo " ✅ ALL DONE! Next steps:"
echo ""
echo " 1. Enable GitHub Pages:"
echo "    https://github.com/$GITHUB_USERNAME/$SITE_REPO/settings/pages"
echo "    → Source: Deploy from branch → main → / (root) → Save"
echo ""
echo " 2. Your URLs (live in ~60 seconds):"
echo "    Website:       https://$GITHUB_USERNAME.github.io/$SITE_REPO/"
echo "    Privacy Policy: https://$GITHUB_USERNAME.github.io/$SITE_REPO/privacy-policy.html"
echo ""
echo " 3. Next: Deploy backend to Railway"
echo "    https://railway.app → New Project → Deploy from GitHub"
echo "    Select: $MAIN_REPO → backend-spring subfolder"
echo "======================================================"

