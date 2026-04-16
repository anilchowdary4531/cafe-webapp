# GitHub Pages Deployment Guide for Sunset Cafe

Publish `play-site/` from the same `cafe-webapp` repository to GitHub Pages.

## Step 1 — Create one repository

1. Go to [github.com](https://github.com) and sign in.
2. Click **New repository**.
3. Name it: `cafe-webapp`
4. Set visibility to **Public** (required for Pages on free plans).
5. Click **Create repository**.

## Step 2 — Push app + site branch

From this project root, run:

```bash
cd /Users/anilkumarthammineni/Downloads/cafe-webapp
./scripts/push-to-github.sh YOUR_GITHUB_USERNAME
```

This pushes:

- app code to `main`
- site files from `play-site/` to `gh-pages`

## Step 3 — Enable GitHub Pages

1. Open `https://github.com/YOUR_GITHUB_USERNAME/cafe-webapp/settings/pages`
2. Under **Source**, choose **Deploy from a branch**.
3. Select branch `gh-pages` and folder `/ (root)`.
4. Click **Save**.
5. Wait ~60 seconds and refresh.

## Step 4 — Your public URLs

After Pages is enabled:

- **Website URL:** `https://YOUR_GITHUB_USERNAME.github.io/cafe-webapp/`
- **Privacy Policy URL:** `https://YOUR_GITHUB_USERNAME.github.io/cafe-webapp/privacy-policy.html`

Use the privacy policy URL in Google Play Console.

## Step 5 — Update Play listing docs

Once you have the live URLs, update:

- `md/mobile/PLAY_STORE_LISTING_TEMPLATE.md`
- `md/mobile/PLAY_CONSOLE_SUBMISSION_CHECKLIST.md`

## Files in play-site/

| File | URL |
|------|-----|
| `index.html` | Website homepage |
| `privacy-policy.html` | Privacy policy (clean, no dev notes) |

