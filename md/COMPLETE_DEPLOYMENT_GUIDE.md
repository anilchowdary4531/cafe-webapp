# Complete Deployment Guide - Sunset Cafe

This guide covers deploying all three components of Sunset Cafe:
1. **Play Site** (GitHub Pages)
2. **Mobile App** (Google Play Store)
3. **Backend** (Railway)

---

## Table of Contents

1. [Quick Start](#quick-start)
2. [GitHub Pages - Play Site](#github-pages---play-site)
3. [Mobile App - Google Play Store](#mobile-app---google-play-store)
4. [Backend - Railway](#backend---railway)
5. [GitHub Actions Workflows](#github-actions-workflows)
6. [Monitoring & Troubleshooting](#monitoring--troubleshooting)

---

## Quick Start

For first-time deployment, follow this order:

```bash
# 1. Set up GitHub repo with all three components
cd /Users/anilkumarthammineni/Downloads/cafe-webapp
git remote add origin https://github.com/YOUR_USERNAME/cafe-webapp.git
git push -u origin main

# 2. Create separate repo for play-site (optional, for isolation)
cd play-site
git init
git remote add origin https://github.com/YOUR_USERNAME/sunset-cafe-site.git
git push -u origin main
cd ..

# 3. Configure secrets for GitHub Actions (see section below)

# 4. Deployments will run automatically on push to main
```

---

## GitHub Pages - Play Site

### Overview
- **Location:** `/play-site`
- **Deployed to:** GitHub Pages
- **URL:** `https://YOUR_USERNAME.github.io/sunset-cafe-site/`
- **Trigger:** Auto-deploy on push to `main` (see `.github/workflows/deploy-play-site.yml`) when using same-repo Pages via GitHub Actions

### Setup Steps

#### Option A: Deploy from Main Repo with GitHub Actions

1. Push this entire repo to GitHub
2. Go to **Settings → Pages**
3. Under "Build and deployment":
   - Source: `GitHub Actions`
4. Click **Save**
5. After 1-3 minutes, your site is live at:
   - Website: `https://YOUR_USERNAME.github.io/cafe-webapp/`
   - Privacy: `https://YOUR_USERNAME.github.io/cafe-webapp/privacy-policy.html`

#### Option B: Deploy from Separate Repo

This provides a cleaner URL and better isolation.

1. Create new repo: `sunset-cafe-site`
2. Push only play-site folder:
   ```bash
   cd play-site
   git init
   git checkout -b main
   git add .
   git commit -m "Add Sunset Cafe website"
   git remote add origin https://github.com/YOUR_USERNAME/sunset-cafe-site.git
   git push -u origin main
   ```
3. Enable GitHub Pages as above
4. URLs become:
   - Website: `https://YOUR_USERNAME.github.io/sunset-cafe-site/`
   - Privacy: `https://YOUR_USERNAME.github.io/sunset-cafe-site/privacy-policy.html`

### Verification

After deployment:
```bash
# Same-repo Pages via GitHub Actions
curl -I https://YOUR_USERNAME.github.io/cafe-webapp/

# Separate sunset-cafe-site repo
curl -I https://YOUR_USERNAME.github.io/sunset-cafe-site/

# Either chosen URL should return 200 OK
```

---

## Mobile App - Google Play Store

### Overview
- **Location:** `/mobile`
- **Build System:** Capacitor + Android Gradle
- **Signing:** Keystore (JKS)
- **CI/CD:** GitHub Actions (`.github/workflows/android-build.yml`)

### Prerequisites

1. **Android Keystore** (one-time setup)
   ```bash
   # Generate keystore (if you don't have one)
   keytool -genkey -v -keystore sunset-cafe-upload-key.jks \
     -keyalg RSA -keysize 2048 -validity 36500 \
     -alias sunset-cafe
   ```

2. **GitHub Actions Secrets**
   
   Add these to your GitHub repo (Settings → Secrets and variables → Actions):
   
   | Secret | Value | Example |
   |--------|-------|---------|
   | `ANDROID_STORE_PASSWORD` | Keystore password | `your-secure-password` |
   | `ANDROID_KEY_PASSWORD` | Key password | `your-key-password` |
   | `ANDROID_KEYSTORE_BASE64` | Base64-encoded keystore | See below |
   | `MOBILE_API_BASE_URL` | Backend API URL | `https://api.railway.app` |

   **To encode keystore as Base64:**
   ```bash
   base64 -i mobile/sunset-cafe-upload-key.jks | pbcopy
   # Paste into GitHub Secrets → ANDROID_KEYSTORE_BASE64
   ```

3. **Google Play Console Account**
   - Create account at https://play.google.com/console
   - Set up app listing
   - Get signing certificate fingerprints from play-site

### Local Testing

Build APK locally:

```bash
# With API URL
./scripts/rebuild-apk.sh https://api.railway.app

# Or set environment variable
export MOBILE_API_BASE_URL="https://api.railway.app"
./scripts/rebuild-apk.sh

# Without API URL (uses config from mobile/www/config.js)
./scripts/rebuild-apk.sh
```

Output:
- APK: `mobile/android/app/build/outputs/apk/release/app-release.apk`
- AAB: `mobile/android/app/build/outputs/bundle/release/app-release.aab`

### Automated CI/CD

**Trigger:** Push to `main` with changes to `mobile/` or `*.html`/`*.js`/`*.css`

**Workflow:** `.github/workflows/android-build.yml`

1. Checks out code
2. Sets up Node.js and JDK 17
3. Installs dependencies
4. Validates API URL (no localhost in production)
5. Syncs Capacitor assets
6. Restores keystore from secrets
7. Builds release AAB
8. Uploads artifact (30-day retention)

**View Results:**
- GitHub Actions tab → Android Build
- Download artifacts: `app-release-aab`

### Manual Google Play Store Upload

1. Go to https://play.google.com/console
2. Select Sunset Cafe app
3. **Production release** → **Create new release**
4. Upload `app-release.aab` from GitHub artifact
5. Review metadata and pricing
6. Submit for review

Expected review time: 24-48 hours

---

## Backend - Railway

### Overview
- **Spring Boot** backend (Java 17)
- **PostgreSQL** database
- **Deployed to:** Railway
- **Packaging workflow:** GitHub Actions (`.github/workflows/backend-deploy.yml`)

### Prerequisites

1. **Railway Account**
   - Sign up at https://railway.app
   - Create a project

2. **PostgreSQL Service on Railway**
   - Add PostgreSQL plugin
   - Note connection details

3. **GitHub Actions Secrets**
   
   | Secret | Value | 
   |--------|-------|
   | `DATABASE_URL` | `jdbc:postgresql://host:port/cafe_webapp` |
   | `DB_USERNAME` | PostgreSQL user |
   | `DB_PASSWORD` | PostgreSQL password |
   | `JWT_SECRET` | 32+ character secret |
   | `CORS_ORIGIN` | Your frontend URL |
   | `OTP_PROVIDER` | `demo`, `twilio`, etc |

### Local Testing

```bash
cd backend-spring

# Build
mvn clean package -DskipTests

# Run with environment
export DATABASE_URL="jdbc:postgresql://localhost:5432/cafe_webapp"
export DB_USERNAME="postgres"
export DB_PASSWORD="password"
export JWT_SECRET="your-secret-minimum-32-characters"
export CORS_ORIGIN="http://localhost:3000"

java -jar target/backend-spring-*.jar
```

### Deployment to Railway

#### Option 1: GitHub Integration (Recommended)

1. Connect Railway to GitHub
2. Select `cafe-webapp` repo
3. Service source: `backend-spring/`
4. Add environment variables from secrets
5. Railway auto-deploys on push to `main`

#### Option 2: Railway CLI

```bash
# Install Railway CLI
npm i -g @railway/cli

# Login and link project
railway login
railway link

# Deploy
railway up
```

#### Option 3: GitHub Actions packaging

The workflow packages the JAR automatically:

```yaml
# .github/workflows/backend-deploy.yml
- Builds JAR with Maven
- Uploads artifact
- Prints Railway deployment reminder
```

### Verification

```bash
# Check health endpoint
curl https://YOUR_RAILWAY_URL.up.railway.app/health

# View logs
railway logs

# SSH into container
railway shell
```

---

## GitHub Actions Workflows

### Summary

| Workflow | Trigger | Action |
|----------|---------|--------|
| `deploy-play-site.yml` | Push to `play-site/` | Deploy to GitHub Pages |
| `test-frontend.yml` | Push to HTML/JS/CSS | Validate frontend code |
| `android-build.yml` | Push to `mobile/` | Build release AAB, upload artifact |
| `backend-ci.yml` | Push to `backend-spring/` | Build & test with Maven |
| `backend-deploy.yml` | Push to `backend-spring/` | Package JAR, remind you to deploy via Railway integration or CLI |

### Viewing Workflow Results

1. Go to **Actions** tab in GitHub
2. Click workflow name
3. Click recent run
4. View logs and artifacts

### Skipping CI/CD

To skip workflows for a commit:

```bash
git commit -m "Quick fix [skip ci]"
git push
```

---

## Monitoring & Troubleshooting

### Play Site Issues

**Issue:** GitHub Pages shows 404

**Solution:**
```bash
# Verify correct folder is deployed
ls -la play-site/
# Should contain: index.html, privacy-policy.html, assets/

# Check settings
# Settings → Pages
# Same-repo workflow: Source = GitHub Actions
# Separate repo flow: Branch = main, Folder = /
```

### Mobile App Build Fails

**Issue:** "Release builds cannot use localhost"

**Solution:** 
```bash
# Set production API URL
./scripts/rebuild-apk.sh https://YOUR_API_URL.com

# Or set environment variable
export MOBILE_API_BASE_URL="https://YOUR_API_URL.com"
./scripts/rebuild-apk.sh
```

**Issue:** "ANDROID_KEYSTORE_BASE64 not found"

**Solution:**
```bash
# Add to GitHub Secrets
base64 -i mobile/sunset-cafe-upload-key.jks | pbcopy
# Settings → Secrets and variables → Actions
# New repository secret: ANDROID_KEYSTORE_BASE64
```

### Backend Deployment Fails

**Issue:** "DATABASE_URL not set"

**Solution:**
```bash
# Add to GitHub Secrets
# DATABASE_URL=jdbc:postgresql://host:port/cafe_webapp

# Or set Railway env var directly
railway env DB_USERNAME postgres
railway env DB_PASSWORD mypassword
```

**Issue:** "Port already in use"

**Solution:**
```bash
# Find process using port 8080
lsof -i :8080

# Kill it
kill -9 <PID>

# Or change port in application.yml
```

### Checking Logs

**GitHub Actions:**
```bash
# View in GitHub UI
# Actions → [Workflow] → [Run] → Logs
```

**Railway:**
```bash
railway logs --follow
```

**Play Site:**
```bash
# Check GitHub Actions deployment logs
# Go to Actions → Deploy Play Site → Recent run
```

---

## Best Practices

1. **Always test locally first**
   ```bash
   ./scripts/rebuild-apk.sh https://test-api.com
   mvn clean package -DskipTests
   ```

2. **Use feature branches for major changes**
   ```bash
   git checkout -b feature/new-menu-items
   # Make changes
   # Open Pull Request
   # Once merged to main, deploys automatically
   ```

3. **Monitor GitHub Actions**
   - Set up email notifications
   - Check Actions tab before pushing
   - Review failed workflow logs

4. **Tag releases**
   ```bash
   git tag -a v1.0.0 -m "Release 1.0.0"
   git push origin v1.0.0
   ```

5. **Keep secrets secure**
   - Never commit `.env` files
   - Use GitHub Secrets for all sensitive data
   - Rotate secrets periodically
   - Audit secret access in Settings

6. **Database backups**
   - Railway auto-backups daily
   - Export data before major migrations
   - Test migrations in staging first

---

## Additional Resources

- [GitHub Pages Documentation](https://docs.github.com/en/pages)
- [Google Play Console Help](https://support.google.com/googleplay/android-developer)
- [Railway Documentation](https://docs.railway.app)
- [Capacitor Documentation](https://capacitorjs.com/docs)
- [Spring Boot Documentation](https://spring.io/projects/spring-boot)

---

## Support

For issues:
1. Check troubleshooting section above
2. Review GitHub Actions logs
3. Check Railway/GitHub Pages status pages
4. Open GitHub issue with error logs

