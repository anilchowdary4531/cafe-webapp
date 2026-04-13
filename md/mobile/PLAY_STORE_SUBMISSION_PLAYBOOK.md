# Sunset Cafe Play Console Submission Playbook

Complete step-by-step guide from now until your app goes live on Google Play.

---

## PHASE 1: Publish Your Website (You do this)

**Timeline:** ~10 minutes + 1 minute GitHub Pages activation

### What you have
- `play-site/index.html` - Your app landing page
- `play-site/privacy-policy.html` - Your privacy policy
- `play-site/DEPLOY.md` - Deployment instructions

### What you do

Follow **`play-site/DEPLOY.md`** exactly:

1. Create repo: `sunset-cafe-site` on GitHub
2. Push `play-site/` files to `main` branch
3. Enable GitHub Pages (Settings → Pages)
4. Wait 60 seconds for it to go live

### What you get (example URLs)

```
Website URL:      https://YOUR_USERNAME.github.io/sunset-cafe-site/
Privacy Policy:   https://YOUR_USERNAME.github.io/sunset-cafe-site/privacy-policy.html
```

Copy these URLs. You'll need them next.

---

## PHASE 2: Update Your Docs

**Timeline:** ~5 minutes

Once you have your live URLs from Phase 1, update:

- `md/mobile/PLAY_STORE_LISTING_TEMPLATE.md`
- `md/mobile/PLAY_CONSOLE_SUBMISSION_CHECKLIST.md`

With your real website and privacy policy URLs.

---

## PHASE 3: Production Backend Deployment

**Timeline:** Variable (depends on your deployment method)

You need a production backend running on HTTPS. Options:

### Option A: Deploy to a cloud platform (recommended)
- **AWS / Azure / Google Cloud**: Full control, but requires account setup
- **Render / Railway / Fly.io**: Easier, good for startups
- **Heroku**: Simple but paid tier required now

### Option B: Run on your own server
- Requires Linux server + SSL certificate (Let's Encrypt free)

### What to do

1. Deploy `backend-spring/` to production
2. Ensure PostgreSQL is running and backed up
3. Set real environment variables:
   ```
   JWT_SECRET=long-random-string
   DATABASE_URL=production-database-url
   CORS_ORIGIN=https://your-domain.com
   OTP_PROVIDER=msg91 (or real SMS provider, not demo)
   ```
4. Test backend health:
   ```bash
   curl https://your-api-domain.com/health
   ```

Once you have production API URL: `https://your-api-domain.com`

---

## PHASE 4: Build Signed App with Production API

**Timeline:** ~5 minutes

Once you have your production backend URL:

```bash
cd /Users/anilkumarthammineni/Downloads/cafe-webapp/mobile
export JAVA_HOME=$(/usr/libexec/java_home -v 21)
MOBILE_API_BASE_URL=https://your-api-domain.com npm run android:sync
npm run android:bundle:release
```

Output will be: `android/app/build/outputs/bundle/release/app-release.aab`

Verify signing:
```bash
jarsigner -verify -verbose -certs android/app/build/outputs/bundle/release/app-release.aab
```

Should say: `jar verified`

---

## PHASE 5: Create Google Play Developer Account

**Timeline:** ~15 minutes + 1-2 days for account approval

1. Go to [Google Play Console](https://play.google.com/console)
2. Sign in with Google account
3. Accept Developer Agreement
4. Pay one-time $25 registration fee
5. Wait 1-2 days for account approval

---

## PHASE 6: Create App Listing in Play Console

**Timeline:** ~20 minutes

Once account is approved and Phase 2 is complete:

### Create new app

1. Click **Create app**
2. Enter:
   - App name: `Sunset Cafe`
   - Default language: `English`
   - App type: `App`
3. Click **Create app**

### App details

1. Fill in **App ID**: `com.sunsetcafe.qrapp` (auto-filled)
2. Upload screenshots:
   - Minimum 2 phone screenshots (1080x1920px)
   - Optional: tablet screenshots, preview video
3. App icon: `512x512` PNG
4. Feature graphic: `1024x500` PNG

### Store listing

Fill in from `md/mobile/PLAY_STORE_LISTING_TEMPLATE.md`:

- **Short description** (80 chars max):
  ```
  Order food and request service directly from your table.
  ```

- **Full description** (4000 chars max):
  ```
  Sunset Cafe lets customers scan a table QR code, browse the live menu, place orders, 
  and request service directly from their phone.
  
  Features:
  - Browse the latest menu with item images
  - View veg and non-veg item labels
  - Place dine-in orders from your table
  - Request staff assistance without waiting
  - Secure login and role-based access for admin and staff
  - Real-time menu visibility controls managed by the restaurant
  
  Sunset Cafe is built for a faster, smoother in-cafe ordering experience for customers, 
  staff, and administrators.
  ```

- **Website**: Your GitHub Pages website URL from Phase 1
- **Email**: `anilchowdarya8@gmail.com`
- **Privacy policy**: Your GitHub Pages privacy policy URL from Phase 1

### Content rating

1. Go to **Content rating**
2. Fill out the questionnaire (5 min)
3. Submit

### App access

If your app requires reviewer test access:

1. Go to **App access**
2. Add reviewer test account (optional)
3. Include instructions:
   ```
   Test Login Instructions:
   
   Customer: Temporary test credentials (do not use production customer data)
   Staff: Temporary reviewer account with staff role
   Admin: Temporary reviewer account with admin role
   ```

### Data safety

1. Go to **Data safety**
2. Declare data collection:
   - Phone number: Yes (for OTP)
   - Email: Yes (for staff/admin)
   - Order data: Yes (for orders)
   - Session tokens: Yes (for auth)
3. Data practices:
   - Data deletion: Provide mechanism or 30-day retention
   - Data encryption: HTTPS in transit

---

## PHASE 7: Upload Release Bundle

**Timeline:** ~5 minutes

1. Go to **Release** → **Production** (or **Internal testing** first)
2. Click **Create new release**
3. Upload your signed `.aab`:
   - `android/app/build/outputs/bundle/release/app-release.aab`
4. Add release notes (optional):
   ```
   Version 1.0.0 - Initial release
   
   Features:
   - QR-based table ordering
   - Live menu with images
   - Service request system
   - Admin and staff roles
   ```
5. Click **Save**

---

## PHASE 8: Test Before Going Live

**Timeline:** ~30 minutes

### Option A: Internal Testing (recommended first)

1. In Play Console, go to **Internal testing**
2. Click **Create new release** (same as Phase 7)
3. Add testers' email addresses
4. Testers click link → Install from Play Store
5. Test on real Android device:
   - Customer login (OTP)
   - Browse menu
   - Place order
   - Request service
   - Admin login (if applicable)

### Option B: Staged Rollout

Start with small percentage (10% → 25% → 50% → 100%):

1. In **Production** release
2. Scroll to **Staged rollout**
3. Start at 10% for 2 days
4. Monitor crash rates and ratings
5. Increase gradually if stable

---

## PHASE 9: Submit for Review

**Timeline:** 1-3 days for Google review

1. In **Production** or **Staged rollout**
2. Click **Send to review**
3. You'll see status: **In review**
4. Google reviews for ~24-72 hours
5. You'll get an email:
   - ✅ **Approved** → Your app goes live!
   - ❌ **Rejected** → Fix issues and resubmit

---

## PHASE 10: Your App Goes Live 🎉

**Timeline:** Minutes after approval

Once approved, your app is live on Google Play:

- Users can search "Sunset Cafe"
- Download from Play Store
- Your website link visible
- Privacy policy accessible

---

## Quick Reference

### Files you need
- ✅ Signed AAB: `mobile/android/app/build/outputs/bundle/release/app-release.aab`
- ✅ Website: Published from `play-site/index.html`
- ✅ Privacy policy: Published from `play-site/privacy-policy.html`
- ✅ Screenshots: 2+ phone screenshots (1080x1920)
- ✅ Icon: 512x512 PNG
- ✅ Feature graphic: 1024x500 PNG

### Things you'll enter in Play Console
- App name: `Sunset Cafe`
- Package: `com.sunsetcafe.qrapp`
- Version: `1.0.0`
- Website: Your GitHub Pages URL
- Email: `anilchowdarya8@gmail.com`
- Privacy policy: Your GitHub Pages privacy URL
- Short description: Order food and request service from your table
- Screenshots: 2+ images showing the app

### Environment variables needed
- Production backend URL (HTTPS)
- JWT_SECRET (long random string)
- DATABASE_URL (PostgreSQL connection)
- CORS_ORIGIN (your domain)
- OTP_PROVIDER (msg91 or similar, not demo)

---

## Estimated Total Time

| Phase | Time | Who |
|-------|------|-----|
| 1. Publish website | 10 min | You |
| 2. Update docs | 5 min | You |
| 3. Deploy backend | 30-120 min | You |
| 4. Build signed app | 5 min | You |
| 5. Create Play account | 15 min + 48h | You |
| 6. Create listing | 20 min | You |
| 7. Upload bundle | 5 min | You |
| 8. Internal testing | 30 min | You |
| 9. Submit review | 0 min | You |
| 10. Wait for approval | 24-72h | Google |
| **TOTAL** | **~3-5 days** | - |

---

## What to do right now

1. Push both repositories with `./scripts/push-to-github.sh anilchowdary4531`
2. Enable GitHub Pages for `sunset-cafe-site`
3. Deploy `backend-spring/` on Railway and get the HTTPS API URL
4. Rebuild AAB with production API: `./scripts/rebuild-aab.sh https://YOUR_RAILWAY_URL.up.railway.app`
5. Complete Play Console listing + upload `app-release.aab`

**Ready?**


