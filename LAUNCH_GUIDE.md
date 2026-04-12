# 🚀 LAUNCH GUIDE — Push to GitHub + Deploy to Railway

Complete step-by-step guide. Follow in order. Takes ~30 minutes.

---

## STEP 1 — Create Two GitHub Repositories

Go to https://github.com/new and create:

| Repo | Visibility | Purpose |
|------|-----------|---------|
| `cafe-webapp` | Private | Full app code |
| `sunset-cafe-site` | **Public** | Website + privacy policy (GitHub Pages) |

---

## STEP 2 — Push Main App to GitHub

Open Terminal and run:

```bash
cd /Users/anilkumarthammineni/Downloads/cafe-webapp

git remote add origin https://github.com/anilchowdary4531/cafe-webapp.git
git push -u origin main
```

✅ Your full app code is now on GitHub.

---

## STEP 3 — Publish Website to GitHub Pages

```bash
cd /Users/anilkumarthammineni/Downloads/cafe-webapp/play-site

git init
git checkout -b main
git add .
git commit -m "Add Sunset Cafe website and privacy policy"
git remote add origin https://github.com/anilchowdary4531/sunset-cafe-site.git
git push -u origin main
```

Then enable Pages:
1. Go to: https://github.com/anilchowdary4531/sunset-cafe-site/settings/pages
2. Branch: `main` → folder: `/` (root) → **Save**
3. Wait 60 seconds

✅ Your public URLs (ready in ~60s):
- **Website:** https://anilchowdary4531.github.io/sunset-cafe-site/
- **Privacy Policy:** https://anilchowdary4531.github.io/sunset-cafe-site/privacy-policy.html

---

## STEP 4 — Add GitHub Actions Secrets (for Android CI builds)

Go to: https://github.com/anilchowdary4531/cafe-webapp/settings/secrets/actions

Add these 4 secrets:

| Secret Name | Value |
|------------|-------|
| `ANDROID_KEYSTORE_BASE64` | *(contents of `scripts/keystore-base64.txt`)* |
| `ANDROID_STORE_PASSWORD` | `sU9dXuwMY0WADnS1VthBPreC` |
| `ANDROID_KEY_ALIAS` | `sunset-cafe` |
| `ANDROID_KEY_PASSWORD` | `sU9dXuwMY0WADnS1VthBPreC` |

To get the keystore base64 value:
```bash
cat /Users/anilkumarthammineni/Downloads/cafe-webapp/scripts/keystore-base64.txt
```
Copy the entire output → paste as the `ANDROID_KEYSTORE_BASE64` secret value.

Also add:
| `MOBILE_API_BASE_URL` | `https://YOUR_RAILWAY_URL.up.railway.app` *(add after Step 5)* |

---

## STEP 5 — Deploy Backend to Railway (Free)

### A. Create Railway Account
1. Go to https://railway.app
2. Click **Login with GitHub**
3. Authorize Railway

### B. Create New Project
1. Click **New Project**
2. Select **Deploy from GitHub repo**
3. Choose `cafe-webapp`
4. When asked for root directory: type `backend-spring`
5. Click **Deploy**

### C. Add PostgreSQL Database
1. In your Railway project, click **+ New**
2. Select **Database** → **PostgreSQL**
3. Railway auto-connects it

### D. Set Environment Variables
In Railway project → Variables tab, add:

```
PORT=4100
DATABASE_URL=<auto-filled by Railway PostgreSQL plugin>
DB_USERNAME=<from PostgreSQL plugin>
DB_PASSWORD=<from PostgreSQL plugin>
JWT_SECRET=<generate: openssl rand -hex 32>
CORS_ORIGIN=https://anilchowdary4531.github.io,capacitor://localhost,http://localhost
OTP_PROVIDER=demo
ADMIN_ACCESS_EMAIL=anilchowdarya8@gmail.com
STAFF_ACCESS_EMAIL=staff@sunsetcafe.local
```

To generate JWT_SECRET, run in Terminal:
```bash
openssl rand -hex 32
```

### E. Get Your Railway URL
After deploy completes:
- Railway gives you a URL like: `https://cafe-webapp-production-xxxx.up.railway.app`
- Test it: `curl https://YOUR_URL.up.railway.app/actuator/health`
- Should return: `{"status":"UP"}`

✅ Backend is live!

---

## STEP 6 — Rebuild Android App with Production URL

```bash
cd /Users/anilkumarthammineni/Downloads/cafe-webapp
./scripts/rebuild-aab.sh https://YOUR_RAILWAY_URL.up.railway.app
```

This updates the app to point to your live backend and rebuilds the signed AAB.

Output: `mobile/android/app/build/outputs/bundle/release/app-release.aab`

---

## STEP 7 — Create Google Play Developer Account ($25)

1. Go to https://play.google.com/console
2. Sign in with Google account (anilchowdarya8@gmail.com)
3. Accept Developer Agreement
4. Pay **$25 one-time** registration fee
5. Wait 1–2 days for approval

---

## STEP 8 — Create App in Play Console

Once account is approved:

1. Click **Create app**
2. Fill in:
   - App name: `Sunset Cafe`
   - Default language: `English (United States)`
   - App type: `App`
   - Free or paid: `Free`
3. Click **Create app**

---

## STEP 9 — Upload Store Assets

### App Icon (512×512)
File: `play-site/assets/icon-512.png`

### Feature Graphic (1024×500)
File: `play-site/assets/feature-1024x500.png`

### Screenshots (minimum 2)
Take screenshots of the running app on your phone or Android emulator.
Size: 1080×1920px minimum.

---

## STEP 10 — Fill Store Listing

In Play Console → Store listing:

**Short description** (80 chars):
```
Order food and request service directly from your table.
```

**Full description**:
```
Sunset Cafe lets customers scan a table QR code, browse the live menu,
place orders, and request service directly from their phone.

Features:
• Browse the menu with photos, veg/non-veg labels, prices
• Place dine-in orders from your table
• Request staff assistance (water, bill, etc.)
• Secure OTP-based customer login
• Role-based access for admin and staff
• Admin controls: enable/disable items, manage staff, tax slabs
• Real-time order tracking for staff

The smart way to dine in — for customers, staff, and administrators.
```

**Website:** `https://anilchowdary4531.github.io/sunset-cafe-site/`
**Email:** `anilchowdarya8@gmail.com`
**Privacy policy:** `https://anilchowdary4531.github.io/sunset-cafe-site/privacy-policy.html`

---

## STEP 11 — Complete Required Declarations

### Content Rating
Play Console → Content rating → Fill questionnaire → Submit
(Will get "Everyone" rating)

### Data Safety
Declare:
- Phone number: Collected (OTP login)
- Email address: Collected (staff/admin)
- User IDs: Collected (session tokens)
- In-app purchase history: Not collected
- Encryption: Yes (HTTPS)

### App Access
Instructions for reviewer:
```
Customer: Enter any phone number → OTP: 123456 (demo mode)
Staff:    Email: staff@sunsetcafe.local → OTP: 123456
Admin:    Email: anilchowdarya8@gmail.com → OTP: 123456
```

---

## STEP 12 — Upload AAB + Internal Testing

1. Play Console → Testing → **Internal testing**
2. Click **Create new release**
3. Upload: `mobile/android/app/build/outputs/bundle/release/app-release.aab`
4. Release notes:
   ```
   Version 1.0.0 — Initial release
   QR-based table ordering, live menu, service requests, admin/staff roles.
   ```
5. Click **Save** → **Review release** → **Start rollout to Internal testing**
6. Add your own email as a tester
7. Open the tester link on your Android phone → Install → Test!

---

## STEP 13 — Promote to Production

Once testing passes:
1. Play Console → Production → **Create new release**
2. Upload same AAB
3. Click **Send for review**
4. Wait 1–7 days for Google review
5. 🎉 **App goes live on Play Store!**

---

## Summary Checklist

- [ ] Repos created on GitHub
- [ ] Code pushed to `cafe-webapp`
- [ ] `sunset-cafe-site` published on GitHub Pages
- [ ] GitHub Actions secrets added
- [ ] Backend deployed on Railway (HTTPS URL obtained)
- [ ] AAB rebuilt with Railway URL
- [ ] Google Play Developer Account created ($25)
- [ ] Store listing filled
- [ ] App icon + feature graphic uploaded
- [ ] Screenshots uploaded (min 2)
- [ ] Content rating completed
- [ ] Data safety form completed
- [ ] AAB uploaded to Internal Testing
- [ ] Internal testing passed on real device
- [ ] Submitted to Production review

---

## Your Key Files

| Purpose | File |
|---------|------|
| Signed AAB | `mobile/android/app/build/outputs/bundle/release/app-release.aab` |
| App icon | `play-site/assets/icon-512.png` |
| Feature graphic | `play-site/assets/feature-1024x500.png` |
| Keystore | `mobile/sunset-cafe-upload-key.jks` |
| Keystore base64 | `scripts/keystore-base64.txt` (for GitHub secret) |
| Website | `play-site/index.html` |
| Privacy policy | `play-site/privacy-policy.html` |

