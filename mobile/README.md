# Sunset Cafe Mobile App

This Capacitor workspace packages the cafe web app for Android and iOS.

## Quick start

```bash
cd /Users/anilkumarthammineni/Downloads/cafe-webapp/mobile
cp .env.example .env
npm install
npm run cap:sync
npm run cap:open:android
```

## Release build

```bash
cd /Users/anilkumarthammineni/Downloads/cafe-webapp/mobile
MOBILE_API_BASE_URL=https://api.example.com npm run android:sync
npm run android:bundle:release
```

## Signing for Play Store upload

Create local signing config from the template:

```bash
cd /Users/anilkumarthammineni/Downloads/cafe-webapp/mobile
cp signing.properties.example signing.properties
```

Set real values in `signing.properties`:

```ini
storeFile=sunset-cafe-upload-key.jks
storePassword=YOUR_STORE_PASSWORD
keyAlias=sunset-cafe
keyPassword=YOUR_KEY_PASSWORD
```

Then build a signed bundle:

```bash
cd /Users/anilkumarthammineni/Downloads/cafe-webapp/mobile
export JAVA_HOME=$(/usr/libexec/java_home -v 21)
MOBILE_API_BASE_URL=https://api.example.com npm run android:sync
npm run android:bundle:release
```

You can also use environment variables instead of `signing.properties`:

- `ANDROID_STORE_FILE`
- `ANDROID_STORE_PASSWORD`
- `ANDROID_KEY_ALIAS`
- `ANDROID_KEY_PASSWORD`

## Versioning before upload

- Keep `mobile/package.json` `version` aligned with release notes.
- Increment Android `versionCode` in `mobile/android/app/build.gradle` for every Play update.
- Update Android `versionName` in `mobile/android/app/build.gradle` for every public release.

## Pre-upload command checklist

```bash
cd /Users/anilkumarthammineni/Downloads/cafe-webapp/mobile
export JAVA_HOME=$(/usr/libexec/java_home -v 21)
MOBILE_API_BASE_URL=https://api.your-domain.com npm run android:sync
npm run android:bundle:release
jarsigner -verify -verbose -certs android/app/build/outputs/bundle/release/app-release.aab | cat
```

## Important files

- `capacitor.config.json`
- `.env.example`
- `signing.properties.example`
- `scripts/sync-web-assets.mjs`
- `www/config.js` (generated)
- `../md/mobile/PLAY_STORE_LAUNCH_GUIDE.md`

