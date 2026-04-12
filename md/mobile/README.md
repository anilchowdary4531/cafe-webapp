# Mobile App (Android + iOS) - Capacitor Setup

This project now includes a `mobile/` workspace that packages your existing web app (`index.html`, `staff.html`, `admin.html`) into native apps for Android and iOS.

## What this does

- Reuses existing UI and business logic from the web app.
- Syncs web files into `mobile/www/`.
- Uses Capacitor to generate native projects.

## Folder overview

- `mobile/package.json` - mobile scripts and dependencies
- `mobile/capacitor.config.json` - Capacitor app config
- `mobile/scripts/sync-web-assets.mjs` - copies web files into `mobile/www`
- `mobile/.env.example` - example mobile environment file for API configuration
- `mobile/www/config.js` - generated runtime API base URL for mobile web layer

## Prerequisites

- Node.js 20+
- Android Studio (for Android build/run)
- Xcode (for iOS build/run, macOS only)

## Quick start

```bash
cd /Users/anilkumarthammineni/Downloads/cafe-webapp/mobile
npm install
npm run cap:sync
```

By default, mobile sync uses `http://localhost:4100` so it matches the Spring backend in `backend-spring/`.

## Add native platforms (first time only)

```bash
cd /Users/anilkumarthammineni/Downloads/cafe-webapp/mobile
npm run cap:add:android
npm run cap:add:ios
npm run cap:sync
```

## Open native IDEs

```bash
cd /Users/anilkumarthammineni/Downloads/cafe-webapp/mobile
npm run cap:open:android
npm run cap:open:ios
```

## Backend URL for mobile

Create a local mobile environment file first:

```bash
cd /Users/anilkumarthammineni/Downloads/cafe-webapp/mobile
cp .env.example .env
```

Set the backend URL in `mobile/.env`:

```dotenv
MOBILE_API_BASE_URL=https://api.example.com
```

Then run sync:

```bash
cd /Users/anilkumarthammineni/Downloads/cafe-webapp/mobile
npm run cap:sync
```

The sync script generates `mobile/www/config.js` with:

```js
window.CAFE_API_BASE_URL = 'https://api.example.com';
```

For real mobile devices, `localhost` points to the phone itself.
Use your computer LAN IP only for device testing, or a deployed HTTPS backend for release builds, for example:

```js
window.CAFE_API_BASE_URL = 'https://api.sunsetcafe.com';
```

You can also override at command time without editing `.env`:

```bash
cd /Users/anilkumarthammineni/Downloads/cafe-webapp/mobile
MOBILE_API_BASE_URL=https://api.example.com npm run cap:sync
```

## Typical dev loop

1. Update web files in project root (`index.html`, `app.js`, etc.)
2. Make sure `mobile/.env` points to the backend you want to test.
3. Sync to mobile:

```bash
cd /Users/anilkumarthammineni/Downloads/cafe-webapp/mobile
npm run cap:sync
```

4. Re-run from Android Studio / Xcode

## Android release bundle

Generate the Android project once:

```bash
cd /Users/anilkumarthammineni/Downloads/cafe-webapp/mobile
npm run cap:add:android
```

Create a release-ready bundle:

```bash
cd /Users/anilkumarthammineni/Downloads/cafe-webapp/mobile
MOBILE_API_BASE_URL=https://api.example.com npm run android:sync
npm run android:bundle:release
```

Open Android Studio if you need signing configuration, icons, or Play upload preparation:

```bash
cd /Users/anilkumarthammineni/Downloads/cafe-webapp/mobile
npm run cap:open:android
```

For the full submission checklist, see:

- `md/mobile/PLAY_STORE_LAUNCH_GUIDE.md`
- `md/mobile/PLAY_STORE_LISTING_TEMPLATE.md`
- `privacy-policy.html`

## Notes

- Current approach is fastest path to mobile for both platforms.
- You can later migrate to fully native screens if needed.

