# Play Console Submission Checklist

Use this as the final gate before uploading to Google Play.

## 1) App Identity (Prefilled)

- [x] App name: `Sunset Cafe`
- [x] Application ID / package: `com.sunsetcafe.qrapp`
- [x] Android `versionCode`: `1`
- [x] Android `versionName`: `1.0.0`
- [x] Mobile package version: `1.0.0`

## 2) Release Build + Signing

- [x] Android project exists in `mobile/android/`
- [x] Release build command works: `npm run android:bundle:release`
- [x] `mobile/signing.properties` created from `mobile/signing.properties.example`
- [x] Keystore file exists: `mobile/sunset-cafe-upload-key.jks`
- [x] Real signing values set (`storeFile`, `storePassword`, `keyAlias`, `keyPassword`)
- [x] Signed AAB verified with `jarsigner -verify`
- [x] Final AAB path confirmed: `mobile/android/app/build/outputs/bundle/release/app-release.aab`

### Build commands

```bash
cd /Users/anilkumarthammineni/Downloads/cafe-webapp/mobile
export JAVA_HOME=$(/usr/libexec/java_home -v 21)
MOBILE_API_BASE_URL=https://YOUR_PROD_API_DOMAIN npm run android:sync
npm run android:bundle:release
jarsigner -verify -verbose -certs android/app/build/outputs/bundle/release/app-release.aab | cat
```

## 3) Backend + API Configuration

- [ ] Production API URL set (HTTPS): `TODO_REQUIRED_BEFORE_SUBMIT`
- [ ] `MOBILE_API_BASE_URL` points to production backend (not localhost) - currently `http://localhost:4100`
- [ ] Backend CORS configured for production origins
- [ ] OTP provider is production-ready (not demo)
- [ ] Backend health endpoint reachable from mobile network

## 4) Privacy + Policy

- [x] Policy source file exists: `privacy-policy.html`
- [x] Replace placeholder support email in `privacy-policy.html`
- [ ] Replace placeholder domain URLs in policy and listing docs
- [ ] Host privacy policy at public URL: `TODO_REQUIRED_BEFORE_SUBMIT`

## 5) Store Listing Content

- [x] Template exists: `md/mobile/PLAY_STORE_LISTING_TEMPLATE.md`
- [x] Support email finalized
- [ ] Website URL finalized
- [ ] Privacy policy URL finalized
- [ ] Short description finalized
- [ ] Full description finalized

## 6) Store Graphics and Media

- [ ] App icon (Play): `512x512`
- [ ] Feature graphic: `1024x500`
- [ ] Minimum 2 phone screenshots
- [ ] Optional tablet screenshots (if declaring support)

## 7) Play Console Compliance

- [ ] Data Safety form completed
- [ ] App content declarations completed
- [ ] Content rating completed
- [ ] Ads declaration completed
- [ ] App access/reviewer instructions prepared (admin/staff test access if needed)

## 8) Rollout Strategy

- [ ] Internal testing release uploaded first
- [ ] Internal tester validation completed
- [ ] Production rollout plan confirmed

## 9) Final Submit Gate

- [x] Signed AAB ready
- [ ] Production API confirmed in build
- [ ] Privacy policy publicly accessible
- [ ] Listing assets uploaded
- [ ] Compliance forms complete
- [ ] Submit to Play review

