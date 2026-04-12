# Google Play Launch Guide for `cafe-webapp`

This guide is tailored to the current repo layout:

- Mobile wrapper: `mobile/`
- Web assets copied into: `mobile/www/`
- Likely production backend: `backend-spring/`
- Android package ID: `com.sunsetcafe.qrapp`
- App name: `Sunset Cafe`

## 1. Prepare the production backend

Before you build the Play Store app, deploy the backend publicly over HTTPS.

Recommended target format:

```text
https://api.your-domain.com
```

### Backend checks

- Deploy `backend-spring/`
- Configure PostgreSQL in production
- Set a strong `JWT_SECRET`
- Set production `DATABASE_URL`, `DB_USERNAME`, `DB_PASSWORD`
- Set production `CORS_ORIGIN`
- Configure OTP provider (`demo` must not be used in production)

Example production-style environment values:

```dotenv
PORT=4100
DATABASE_URL=jdbc:postgresql://db-host:5432/cafe_webapp
DB_USERNAME=postgres
DB_PASSWORD=replace-me
CORS_ORIGIN=https://your-website.com,capacitor://localhost,http://localhost
OTP_PROVIDER=msg91
JWT_SECRET=replace-with-a-long-random-secret-value-at-least-32-characters
ADMIN_ACCESS_EMAIL=anilchowdarya8@gmail.com
STAFF_ACCESS_EMAIL=staff@sunsetcafe.local
```

## 2. Point the mobile app to production

Create the mobile environment file:

```bash
cd /Users/anilkumarthammineni/Downloads/cafe-webapp/mobile
cp .env.example .env
```

Set your real API URL:

```dotenv
MOBILE_API_BASE_URL=https://api.your-domain.com
```

Sync the mobile app:

```bash
cd /Users/anilkumarthammineni/Downloads/cafe-webapp/mobile
npm run cap:sync
```

## 3. Generate and open the Android project

The Android project can be generated from the Capacitor workspace:

```bash
cd /Users/anilkumarthammineni/Downloads/cafe-webapp/mobile
npm run cap:add:android
npm run cap:open:android
```

## 4. Android Studio release setup

In Android Studio, verify these items:

- Application ID: `com.sunsetcafe.qrapp`
- App name: `Sunset Cafe`
- Version code increments for every release
- Version name is readable, e.g. `1.0.0`
- App icon is branded and high quality
- Release build uses the production backend URL

## 5. Create the upload keystore

Run on macOS:

```bash
keytool -genkeypair -v \
-keystore /Users/anilkumarthammineni/Downloads/cafe-webapp/mobile/sunset-cafe-upload-key.jks \
-keyalg RSA \
-keysize 2048 \
-validity 10000 \
-alias sunset-cafe
```

Store these safely:

- keystore file
- alias
- keystore password
- key password

## 6. Configure signing for Gradle release builds

Copy signing template:

```bash
cd /Users/anilkumarthammineni/Downloads/cafe-webapp/mobile
cp signing.properties.example signing.properties
```

Set real values in `mobile/signing.properties`:

```ini
storeFile=sunset-cafe-upload-key.jks
storePassword=YOUR_STORE_PASSWORD
keyAlias=sunset-cafe
keyPassword=YOUR_KEY_PASSWORD
```

Alternative (good for CI): set environment variables instead:

- `ANDROID_STORE_FILE`
- `ANDROID_STORE_PASSWORD`
- `ANDROID_KEY_ALIAS`
- `ANDROID_KEY_PASSWORD`

## 7. Build the Android App Bundle

Sync first with the production API URL:

```bash
cd /Users/anilkumarthammineni/Downloads/cafe-webapp/mobile
MOBILE_API_BASE_URL=https://api.your-domain.com npm run android:sync
```

Build the release bundle:

```bash
cd /Users/anilkumarthammineni/Downloads/cafe-webapp/mobile
export JAVA_HOME=$(/usr/libexec/java_home -v 21)
npm run android:bundle:release
```

If signing is not configured yet, Gradle still produces an unsigned bundle for testing.

Optional Android Studio signing flow:

- Build
- Generate Signed Bundle / APK
- Android App Bundle
- Select your keystore
- Build `release`

## 8. Create the Play Console listing

Prepare these before submission:

- App name
- Short description
- Full description
- App icon (512x512)
- Feature graphic (1024x500)
- Phone screenshots
- Support email
- Privacy policy URL

Use:

- `md/mobile/PLAY_STORE_LISTING_TEMPLATE.md`
- `privacy-policy.html`

## 9. Data safety and policy declarations

This app likely processes:

- Phone number
- OTP/session data
- Order information
- User role data (admin/staff/customer)

Review Play Console sections carefully:

- Data safety
- App content
- Content rating
- Ads declaration
- App access if admin/staff login is required for review

## 10. Test before production

Use Internal Testing first.

Verify on a real Android phone:

- Customer login and OTP flow
- Menu loads from production backend
- Menu images render
- Orders can be created
- Staff/admin logins work
- Admin-only actions are blocked for non-admin users
- No `localhost` API references remain in the release build

## 11. Final release checklist

- [ ] Production backend deployed over HTTPS
- [ ] `mobile/.env` points to production API
- [ ] `npm run cap:sync` completed successfully
- [ ] Android build opens in Android Studio
- [ ] Signed `.aab` generated
- [ ] Privacy policy URL hosted
- [ ] Store listing assets prepared
- [ ] Data safety form completed
- [ ] Internal testing passed
- [ ] Production rollout submitted

