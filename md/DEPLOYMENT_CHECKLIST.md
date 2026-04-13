# Deployment Checklist

Complete guide to deploy Sunset Cafe to production in the correct order.

---

## Pre-Deployment (One-Time Setup)

### ☐ GitHub Repository
- [ ] Repository created and code pushed
  ```bash
  git remote add origin https://github.com/YOUR_USERNAME/cafe-webapp.git
  git push -u origin main
  ```
- [ ] Repository is public (needed for GitHub Pages)
- [ ] Branch protection enabled for `main` (optional but recommended)

### ☐ GitHub Secrets Configuration
Navigate to **Settings → Secrets and variables → Actions**

#### Mobile Secrets
- [ ] `ANDROID_STORE_PASSWORD` - Your keystore password
- [ ] `ANDROID_KEY_PASSWORD` - Your key password
- [ ] `ANDROID_KEYSTORE_BASE64` - Base64 keystore (see LOCAL_DEVELOPMENT_SETUP.md)
- [ ] `MOBILE_API_BASE_URL` - `https://api.railway.app` (update with your domain)

#### Backend Secrets
- [ ] `DATABASE_URL` - PostgreSQL connection string
- [ ] `DB_USERNAME` - Database username
- [ ] `DB_PASSWORD` - Database password
- [ ] `JWT_SECRET` - 32+ character random string
- [ ] `CORS_ORIGIN` - Your frontend URL
- [ ] `OTP_PROVIDER` - `demo`, `twilio`, etc.

### ☐ Google Play Console Setup
- [ ] Google Play Developer account created
- [ ] Payment method verified
- [ ] Sunset Cafe app created
- [ ] App signed in (generate signing cert)

### ☐ Railway Setup
- [ ] Railway account created
- [ ] Cafe project created
- [ ] PostgreSQL service added
- [ ] Environment variables configured

---

## Deployment Phase 1: Frontend

### ☐ Deploy Play Site (GitHub Pages)

**Estimated time: 5 minutes**

```bash
# Option A: Deploy from main repo
cd /Users/anilkumarthammineni/Downloads/cafe-webapp
git push origin main
```

Then in GitHub:
1. Settings → Pages
2. If using this repo's workflow, set Source to `GitHub Actions`
3. If using a separate `sunset-cafe-site` repo, use `Deploy from a branch` → `main` → `/` (root)
4. Click **Save**

**Verify:**
```bash
# Same-repo Pages via GitHub Actions
curl -I https://YOUR_USERNAME.github.io/cafe-webapp/

# Separate sunset-cafe-site repo
curl -I https://YOUR_USERNAME.github.io/sunset-cafe-site/

# The URL for the option you chose should return 200 OK
```

- [ ] Website live at GitHub Pages
- [ ] Privacy policy accessible at `/privacy-policy.html`
- [ ] All assets loading (images, CSS, JS)

---

## Deployment Phase 2: Mobile App

### ☐ Build APK Locally (Testing)

**Estimated time: 15 minutes**

```bash
cd /Users/anilkumarthammineni/Downloads/cafe-webapp

# Ensure backend URL is production
./scripts/rebuild-apk.sh https://YOUR_RAILWAY_URL.up.railway.app
```

Expected output:
```
✓ APK Build Complete
Output: mobile/android/app/build/outputs/apk/release/app-release.apk
Size: XXX MB
```

- [ ] APK builds successfully locally
- [ ] No localhost in config
- [ ] Signature verified

### ☐ Automated Build via GitHub Actions

**Estimated time: 15 minutes**

1. Push changes to trigger workflow (if not already)
   ```bash
   git push origin main
   ```

2. Go to **Actions** tab
3. Select **Build Android Release AAB**
4. Click **Run workflow** 
5. Choose input:
   - api_url: `https://YOUR_RAILWAY_URL.up.railway.app`
6. Click **Run workflow**

**Monitor:**
- [ ] Workflow starts (green)
- [ ] No CORS/localhost errors
- [ ] Artifact uploaded
- [ ] AAB file available for download

### ☐ Upload to Google Play Console

**Estimated time: 20 minutes**

1. Go to https://play.google.com/console
2. Select Sunset Cafe app
3. **Releases → Production → Create new release**
4. **Upload AAB:**
   - Download `app-release-aab` from GitHub Actions
   - Upload to Play Console
5. **Fill in Release Notes** (what's new)
6. **Review content rating** (if first time)
7. **Check app details** (privacy policy, etc)
8. **Review pricing & distribution**
9. Click **Save and review**
10. Click **Start rollout to Production**

**Status:**
- [ ] AAB uploaded
- [ ] Release notes added
- [ ] No warnings/errors
- [ ] Rollout submitted
- [ ] App queued for review (24-48 hours)

---

## Deployment Phase 3: Backend

### ☐ Deploy Backend Locally (Testing)

**Estimated time: 20 minutes**

```bash
cd backend-spring

# Create application-prod.yml
cat > src/main/resources/application-prod.yml << 'EOF'
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/cafe_webapp
    username: postgres
    password: your-password
  jpa:
    hibernate:
      ddl-auto: update

app:
  jwt:
    secret: your-secret-minimum-32-characters
  cors-origin: https://YOUR_GITHUB_PAGES_URL
  otp:
    provider: demo

server:
  port: 8080
EOF

# Build
mvn clean package -DskipTests

# Test locally
java -jar target/backend-spring-*.jar --spring.profiles.active=prod
```

Test endpoint:
```bash
curl http://localhost:8080/health
# Should return 200 OK
```

- [ ] Backend builds successfully
- [ ] No compilation errors
- [ ] Health endpoint responds
- [ ] Database connection works

### ☐ Deploy to Railway

**Estimated time: 10 minutes (first time) or 5 minutes (automated)**

#### Option 1: GitHub Integration (Recommended)

1. Go to Railway Dashboard
2. **Create new project → GitHub repo**
3. Select `cafe-webapp`
4. Choose **backend-spring** service source
5. Add environment variables (from GitHub Secrets)
6. Deploy

#### Option 2: Railway CLI

```bash
cd backend-spring

# Install Railway CLI
npm i -g @railway/cli

# Login and link
railway login
railway link

# Deploy
railway up
```

#### Option 3: Manual via Actions

1. GitHub Actions packages the JAR automatically
2. Download `cafe-backend-jar` artifact if you want to inspect the packaged build
3. Deploy to Railway using Railway GitHub integration or `railway up`

**Verify:**
```bash
# Wait 2-3 minutes for deployment
railway logs --follow

# Check health endpoint
curl https://YOUR_RAILWAY_URL.up.railway.app/health
# Should return 200 OK

# View live logs
railway logs
```

- [ ] Backend deployed to Railway
- [ ] Health endpoint responding
- [ ] Database connected
- [ ] CORS configured correctly
- [ ] Environment variables set

---

## Post-Deployment Verification

### ☐ Test Complete Integration

**Estimated time: 15 minutes**

1. **Test Frontend:**
   ```bash
   open https://YOUR_USERNAME.github.io/sunset-cafe-site/
   ```
   - [ ] Website loads
   - [ ] All images visible
   - [ ] CSS applied correctly
   - [ ] Privacy policy accessible

2. **Test Backend:**
   ```bash
   # Health check
   curl https://YOUR_RAILWAY_URL.up.railway.app/health
   
   # Test API endpoint
   curl https://YOUR_RAILWAY_URL.up.railway.app/api/menu
   ```
   - [ ] API responding
   - [ ] CORS headers present
   - [ ] Database working
   - [ ] Logs showing requests

3. **Test Mobile (When Approved):**
   - Download app from Play Store
   - Login with test account
   - Place order
   - Check backend logs for requests
   - [ ] App working end-to-end
   - [ ] API calls successful
   - [ ] No error messages

### ☐ Monitoring & Alerts

Set up monitoring (Optional but recommended):

1. **GitHub Actions:**
   - Settings → Notifications
   - Enable email for failures

2. **Railway:**
   - Project settings → Alerts
   - Enable for deployment failures

3. **Google Play Console:**
   - Monitor crash rate
   - Monitor ratings

---

## Rollback Procedures

### If GitHub Pages breaks:
```bash
git revert COMMIT_HASH
git push origin main
# Fixed in 1-3 minutes
```

### If Backend fails:
```bash
# Via Railway CLI
railway logs  # Check error
railway env   # Check configuration
railway down  # Stop deployment

# Or redeploy previous version from Railway Dashboard
```

### If Mobile app crashes:
```bash
# Go to Play Console
# Releases → Production → Pause rollout
# Submit newer APK with fix
```

---

## Post-Launch Checklist

### ☐ Day 1 (After Launch)
- [ ] Monitor Play Store reviews
- [ ] Check backend error logs
- [ ] Test on multiple devices
- [ ] Verify database backups

### ☐ Week 1
- [ ] Gather user feedback
- [ ] Fix any reported bugs
- [ ] Monitor performance metrics
- [ ] Check GitHub Actions health

### ☐ Month 1
- [ ] Review analytics
- [ ] Plan next features
- [ ] Optimize slow endpoints
- [ ] Update documentation

---

## Common Issues During Deployment

### Issue: "Release builds cannot use localhost"

**Solution:**
```bash
./scripts/rebuild-apk.sh https://YOUR_ACTUAL_RAILWAY_URL.up.railway.app
```

### Issue: "ANDROID_KEYSTORE_BASE64 not found"

**Solution:** Add to GitHub Secrets
```bash
base64 -i mobile/sunset-cafe-upload-key.jks | pbcopy
# Paste into GitHub Secrets
```

### Issue: "Database connection failed"

**Solution:** Check DATABASE_URL in Railway
```bash
railway env
# Verify DATABASE_URL matches pattern:
# jdbc:postgresql://HOST:5432/cafe_webapp
```

### Issue: "App rejected from Play Store"

**Solution:** Common reasons:
- Privacy policy not accessible (ensure GitHub Pages deployed)
- Inappropriate content (check menu items)
- Insufficient permissions (check Android manifest)
- Contact Play Support for details

### Issue: "Deploy fails, Railway stuck"

**Solution:**
```bash
railway down
# Wait 2 minutes
railway up
```

---

## Final Checklist

### Before Announcing Launch

- [ ] All three components deployed
- [ ] All secrets configured
- [ ] End-to-end testing complete
- [ ] No error logs
- [ ] Health checks passing
- [ ] Database backups configured
- [ ] Monitoring set up
- [ ] Contact info on play site
- [ ] Privacy policy visible
- [ ] Terms of service ready (if needed)
- [ ] Analytics configured (optional)
- [ ] Support email configured

### After Announcing Launch

- [ ] Monitor initial users
- [ ] Track crash rates
- [ ] Respond to feedback
- [ ] Plan hot-fixes if needed
- [ ] Document lessons learned
- [ ] Plan next features

---

## Support

If deployment fails:

1. **Check logs:**
   - GitHub Actions: Actions → Workflow → Job
   - Railway: `railway logs`
   - Browser: DevTools Console (F12)

2. **Review relevant docs:**
   - LOCAL_DEVELOPMENT_SETUP.md
   - COMPLETE_DEPLOYMENT_GUIDE.md
   - GITHUB_ACTIONS_SETUP.md

3. **Open GitHub issue with:**
   - Error message
   - Steps to reproduce
   - Full logs
   - Environment details

---

## Timeline Estimate

| Phase | Time | Status |
|-------|------|--------|
| Pre-deployment setup | 30 min | ☐ |
| Deploy Play Site | 5 min | ☐ |
| Deploy Mobile | 20 min | ☐ |
| Deploy Backend | 10 min | ☐ |
| Verification | 15 min | ☐ |
| **Total** | **80 min** | ☐ |

---

**You're ready to launch! 🚀**

