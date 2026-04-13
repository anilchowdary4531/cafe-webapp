# 10-Min Android Verification (Internal Testing)

Use this checklist after all deployments are live.

## 0) Expected inputs

- Live backend URL (example: `https://your-backend.up.railway.app`)
- Uploaded Android AAB in Play Console Internal Testing
- Tester Gmail added in Play Console testers list

## 1) Hard pre-check (2 minutes)

- [ ] `mobile/www/config.js` has production API URL (not localhost)
- [ ] Backend health endpoint works publicly
- [ ] Backend CORS includes web and Capacitor origins

Run:

```bash
curl -i https://YOUR_BACKEND_URL/health
```

Pass:
- HTTP `200` and health JSON/body returned

Fail quick fix:
- If 404/502/timeout, fix backend deployment before mobile testing.

## 2) Confirm mobile build points to production (2 minutes)

From repo root:

```bash
cd /Users/anilkumarthammineni/Downloads/cafe-webapp/mobile
MOBILE_API_BASE_URL=https://YOUR_BACKEND_URL npm run cap:sync
cat /Users/anilkumarthammineni/Downloads/cafe-webapp/mobile/www/config.js
```

Pass:
- `window.CAFE_API_BASE_URL` shows your live HTTPS URL
- No `localhost:4100` in `mobile/www/config.js`

Fail quick fix:
- Update `mobile/.env` with `MOBILE_API_BASE_URL=https://YOUR_BACKEND_URL`
- Re-run `npm run cap:sync`

## 3) Play Console release status (2 minutes)

Play Console -> App -> Testing -> Internal testing:

- [ ] Latest release status is available to testers
- [ ] Your tester email is added to test list
- [ ] You accepted testing invite from same Gmail on phone

Pass:
- Tester link opens Play listing with Install button

Fail quick fix:
- Re-add tester email, save list, resend/refresh invite, wait 5-15 minutes

## 4) On-device smoke test (3 minutes)

On Android phone (installed test app):

- [ ] Open app -> menu loads
- [ ] Place one test order
- [ ] Verify order visible in staff/admin view
- [ ] Trigger payment-complete flow used in your app
- [ ] Verify order completion state updates

Pass:
- No API/network errors, order lifecycle completes end-to-end

Fail quick fix:
- Menu not loading: API URL wrong or backend down
- Auth/OTP failure: OTP provider config invalid or blocked
- Role issues: admin/staff emails not set as configured in backend env

## 5) Known blocker map (1 minute)

- `localhost` still in app:
  - Rebuild with production URL and re-upload AAB.
- CORS blocked in logs:
  - Ensure backend `CORS_ORIGIN` includes:
    - your web domain
    - `capacitor://localhost`
    - `http://localhost`
- App installs but cannot call backend:
  - Confirm backend URL is HTTPS and reachable from mobile network.

## Done criteria

You are good to promote to Production when all are true:

- [ ] Health endpoint stable
- [ ] Internal tester can install and use app
- [ ] Customer order flow works end-to-end
- [ ] Staff/admin can see and manage same order
- [ ] No localhost/CORS errors

