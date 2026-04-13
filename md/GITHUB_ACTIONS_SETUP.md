# GitHub Actions Setup Guide

This document explains how to configure GitHub Actions for automated deployment of Sunset Cafe.

---

## Overview

We have 5 workflows set up:

| Workflow | File | Purpose |
|----------|------|---------|
| Deploy Play Site | `deploy-play-site.yml` | Auto-deploy to GitHub Pages |
| Test Frontend | `test-frontend.yml` | Validate HTML/CSS/JS |
| Build Android Release | `android-build.yml` | Build signed APK/AAB |
| Build Spring Boot | `backend-ci.yml` | Build & test backend |
| Package Spring Boot | `backend-deploy.yml` | Build JAR artifact for Railway deployment |

---

## Secrets Configuration

### Step 1: Navigate to Secrets

1. Go to your GitHub repo
2. **Settings** → **Secrets and variables** → **Actions**
3. Click **New repository secret**

### Step 2: Add Required Secrets

#### For Mobile App Builds

```
ANDROID_STORE_PASSWORD
ANDROID_KEY_PASSWORD  
ANDROID_KEYSTORE_BASE64
MOBILE_API_BASE_URL
```

**How to get ANDROID_KEYSTORE_BASE64:**

```bash
# If you have the keystore file:
base64 -i mobile/sunset-cafe-upload-key.jks

# Copy output and paste into GitHub Secret
```

#### For Backend Deployment

```
DATABASE_URL
DB_USERNAME
DB_PASSWORD
JWT_SECRET
CORS_ORIGIN
OTP_PROVIDER
```

Example values:

```
DATABASE_URL = jdbc:postgresql://railway-host:5432/cafe_webapp
DB_USERNAME = postgres
DB_PASSWORD = your-secure-password
JWT_SECRET = your-secret-minimum-32-characters-long
CORS_ORIGIN = https://your-frontend-url.com
OTP_PROVIDER = twilio
```

---

## Workflow Files

All workflows are in `.github/workflows/`

### 1. deploy-play-site.yml

**Triggers on:**
- Push to `main` with changes in `play-site/`
- Manual dispatch

**What it does:**
1. Checks out code
2. Uploads `play-site/` to GitHub Pages
3. Site goes live from this repo via GitHub Pages Actions, typically at `https://YOUR_USERNAME.github.io/cafe-webapp/`

**Important:**
- For this workflow, GitHub Pages should use **GitHub Actions** as the source.
- If you prefer a separate public repo such as `sunset-cafe-site` with branch-based Pages deployment, follow `play-site/DEPLOY.md` instead.

**Permissions required:**
- Read contents
- Write pages
- Write deployments

---

### 2. test-frontend.yml

**Triggers on:**
- Push to `main` with changes to `.html`, `.js`, or `.css`
- Pull requests with same changes

**What it does:**
1. Validates HTML syntax
2. Checks CSS for empty properties
3. Basic frontend checks

**No secrets needed**

---

### 3. android-build.yml

**Triggers on:**
- Push to `main` with changes in `mobile/`
- Manual dispatch with API URL

**What it does:**
1. Sets up Node.js and JDK 17
2. Installs mobile dependencies
3. Validates API URL (no localhost)
4. Syncs Capacitor assets
5. Restores keystore from secret
6. Builds release AAB
7. Uploads artifact (30-day retention)

**Secrets needed:**
```
ANDROID_STORE_PASSWORD
ANDROID_KEY_PASSWORD
ANDROID_KEYSTORE_BASE64
MOBILE_API_BASE_URL
```

**Artifacts:**
- Download from Actions tab
- File: `app-release-aab`
- Uploaded to Google Play Console

---

### 4. backend-ci.yml

**Triggers on:**
- Push to `main` with changes in `backend-spring/`
- Pull requests with same changes

**What it does:**
1. Sets up PostgreSQL service
2. Sets up JDK 17
3. Builds with Maven
4. Runs tests
5. Uploads JAR artifact

**Secrets needed:**
- None for this workflow as currently written.
- It uses the local PostgreSQL service and CI-safe environment values defined inside the workflow.

---

### 5. backend-deploy.yml

**Triggers on:**
- Push to `main` with changes in `backend-spring/`
- Manual dispatch

**What it does:**
1. Builds JAR
2. Uploads artifact
3. Prints Railway packaging/deployment instructions

**Important:**
- This workflow does **not** deploy directly to Railway.
- Use Railway GitHub integration pointing at `backend-spring/`, or use Railway CLI: `railway up`.

---

## Manual Workflow Dispatch

You can manually trigger any workflow from GitHub UI:

1. Go to **Actions** tab
2. Select workflow name (e.g., "Build Android Release AAB")
3. Click **Run workflow**
4. Enter any required inputs
5. Click **Run workflow**

Example: Manually rebuild APK with specific API URL

1. Actions → Build Android Release AAB
2. Run workflow
3. Enter API URL: `https://production-api.railway.app`
4. Click Run workflow

---

## Viewing Workflow Results

### Successful Run

```
✅ Workflow completed successfully
```

You'll see:
- Green checkmark in Actions tab
- Build artifacts available for download
- Deployment live (if applicable)

### Failed Run

```
❌ Workflow failed
```

Steps:
1. Click on failed workflow
2. Click on failed job
3. Scroll to see error details
4. Check logs for specific error message
5. Fix code locally and push again

### Common Errors

**Error: "Release builds cannot use localhost API URLs"**
- Solution: Pass production API URL to workflow

**Error: "ANDROID_KEYSTORE_BASE64 not found"**
- Solution: Add secret to repo Settings

**Error: "DATABASE_URL is required"**
- Solution: Add database secrets

**Error: "Build failed: Maven dependency not found"**
- Solution: Check internet connection, retry

---

## Workflow Status Badges

Add badges to your README to show workflow status:

```markdown
![Deploy Play Site](https://github.com/YOUR_USERNAME/cafe-webapp/actions/workflows/deploy-play-site.yml/badge.svg)
![Test Frontend](https://github.com/YOUR_USERNAME/cafe-webapp/actions/workflows/test-frontend.yml/badge.svg)
![Build Android](https://github.com/YOUR_USERNAME/cafe-webapp/actions/workflows/android-build.yml/badge.svg)
![Build Backend](https://github.com/YOUR_USERNAME/cafe-webapp/actions/workflows/backend-ci.yml/badge.svg)
```

---

## Debugging Workflows

### Enable Debug Logging

1. Go to **Settings** → **Secrets and variables** → **Actions**
2. Add new secret: `ACTIONS_STEP_DEBUG` = `true`
3. Re-run workflow
4. View expanded logs

### View Full Logs

1. Click on workflow run
2. Click on job
3. Click on step
4. See detailed output

### Test Locally

Before pushing:

```bash
# Test Android build
./scripts/rebuild-apk.sh https://test-api.com

# Test backend build
cd backend-spring
mvn clean package -DskipTests

# Test frontend
npm run test  # if available
```

---

## Best Practices

1. **Keep secrets secure**
   - Never log secrets
   - Use `secrets.SECRET_NAME` in workflows
   - GitHub automatically redacts in logs

2. **Limit workflow permissions**
   - Only request needed permissions
   - Default: read contents only

3. **Cache dependencies**
   - Workflows cache npm/maven dependencies
   - Speeds up subsequent builds

4. **Use branch protections**
   - Require passing workflows before merge
   - Settings → Branches → Add rule
   - Require status checks to pass

5. **Monitor workflow costs**
   - GitHub Actions: 2,000 free minutes/month
   - Each workflow minute counts
   - View in Settings → Billing

---

## Troubleshooting Workflows

### Workflow doesn't trigger

**Check:**
- Is branch correct? (should be `main`)
- Are path filters correct?
- Was push actually made?
- Is workflow file valid YAML?

**Test:**
```bash
# Check workflow file syntax
npm install -g ajv-cli
ajv validate -s schema.json -d .github/workflows/your-workflow.yml
```

### Workflow runs but artifact not created

**Check:**
- Does artifact path exist?
- Is upload step running?
- Check artifact upload logs

**Example error:** `Error: No files found with path mobile/android/app/build/outputs/apk/release/app-release.apk`

**Solution:** Build failed earlier - check build step logs

### Secrets not being used

**Check:**
- Correct secret name (case-sensitive)
- Using `${{ secrets.NAME }}` syntax
- Secret added to repo, not organization

---

## Next Steps

1. Add secrets to your GitHub repo
2. Push code to trigger workflows
3. Monitor Actions tab for results
4. Download artifacts as needed
5. Deploy to platforms (Play Store, Railway, etc.)

See [COMPLETE_DEPLOYMENT_GUIDE.md](./COMPLETE_DEPLOYMENT_GUIDE.md) for platform-specific instructions.

