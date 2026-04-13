#!/bin/bash
# setup-github.sh — Initialize git, push main app to GitHub, deploy play-site to GitHub Pages
# Usage: ./setup-github.sh YOUR_GITHUB_USERNAME
# Example: ./setup-github.sh anilchowdary4531

set -euo pipefail

GITHUB_USERNAME="${1:-anilchowdary4531}"
MAIN_REPO="cafe-webapp"
SITE_REPO="sunset-cafe-site"

SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"
ROOT="$(cd "$SCRIPT_DIR/.." && pwd)"

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
echo " Sunset Cafe — GitHub Setup Script"
echo " GitHub Username: $GITHUB_USERNAME"
echo "======================================================"
echo ""

# ── 1. Initialize main repo ──────────────────────────────────────────────────
echo "▶ Step 1: Initializing main git repository..."
cd "$ROOT"

if [ ! -d ".git" ]; then
  git init
  git checkout -B main
  echo "  ✅ Git initialized"
else
  echo "  ℹ️  Git already initialized"
fi

if ! git config user.email >/dev/null; then
  git config user.email "anilchowdarya8@gmail.com"
fi

if ! git config user.name >/dev/null; then
  git config user.name "Anil Chowdary"
fi

git add -A
if ! git diff --cached --quiet; then
  git commit -m "Initial commit: Sunset Cafe full stack app"
  echo "  ✅ Changes committed"
else
  echo "  ℹ️  No new changes to commit"
fi

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
if git remote get-url origin >/dev/null 2>&1; then
  git remote set-url origin "git@github.com:$GITHUB_USERNAME/$MAIN_REPO.git"
else
  git remote add origin "git@github.com:$GITHUB_USERNAME/$MAIN_REPO.git"
fi
git push -u origin main
echo "  ✅ Main repo pushed: https://github.com/$GITHUB_USERNAME/$MAIN_REPO"

# ── 3. Push play-site to separate repo ──────────────────────────────────────
echo ""
echo "▶ Step 4: Publishing play-site to GitHub Pages..."
cp -R "$PLAY_SITE_SOURCE/." "$PLAY_SITE_TEMP/"
cd "$PLAY_SITE_TEMP"

git init
git checkout -B main
git config user.email "anilchowdarya8@gmail.com"
git config user.name "Anil Chowdary"

git add -A
git commit -m "Add Sunset Cafe website and privacy policy"

if git remote get-url origin >/dev/null 2>&1; then
  git remote set-url origin "git@github.com:$GITHUB_USERNAME/$SITE_REPO.git"
else
  git remote add origin "git@github.com:$GITHUB_USERNAME/$SITE_REPO.git"
fi
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

