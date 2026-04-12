# Twilio Free Trial SMS OTP Setup

Get **real SMS messages** with OTP codes sent to customer phones. Free trial includes $15 credit.

---

## 📱 Step 1: Create Twilio Free Trial Account

1. Go to [twilio.com/try-twilio](https://www.twilio.com/try-twilio)
2. Sign up with:
   - Email
   - Password
   - Phone number (for verification)
3. Click **Verify Phone Number** (they'll send a code via SMS)
4. Enter the code and verify

---

## 🔑 Step 2: Get Your Credentials

After signup, you'll see your **Account Dashboard**:

1. **Account SID** - Copy from top of page (starts with `AC...`)
2. **Auth Token** - Click eye icon to reveal (keep secret!)
3. **Phone Number** - You need to request a Twilio phone number

### Get a Free Twilio Phone Number:

1. In Twilio console, go to **Phone Numbers**
2. Click **Get a Number**
3. Select your country
4. Twilio will give you a free number (like `+1234567890`)
5. Click **Accept** to keep it

---

## 📝 Step 3: Update Backend Configuration

Copy your 3 credentials to `backend/.env`:

```bash
nano /Users/anilkumarthammineni/Downloads/cafe-webapp/backend/.env
```

Update these lines:

```dotenv
DATABASE_URL="postgresql://anilkumarthammineni@localhost:5432/cafe_webapp?schema=public"
PORT=4000
CORS_ORIGIN="http://localhost:8000,http://localhost,capacitor://localhost"
SMTP_HOST="smtp.gmail.com"
SMTP_PORT=587
SMTP_USER="anilchowdarya8@gmail.com"
SMTP_PASS="[YOUR_GMAIL_APP_PASSWORD]"
EMAIL_FROM="Sunset Cafe <anilchowdarya8@gmail.com>"
TWILIO_ACCOUNT_SID="ACxxxxxxxxxxxxxxxxxxxxxxxx"
TWILIO_AUTH_TOKEN="your-auth-token-here"
TWILIO_PHONE_NUMBER="+1234567890"
OTP_MODE="sms"
```

**Replace:**
- `TWILIO_ACCOUNT_SID` - Your Account SID from dashboard
- `TWILIO_AUTH_TOKEN` - Your Auth Token
- `TWILIO_PHONE_NUMBER` - Your Twilio phone number
- `OTP_MODE` - Set to `"sms"` for real SMS (or `"demo"` for on-screen OTP)

**Save:** Press `Ctrl+X`, then `Y`, then `Enter`

---

## 🚀 Step 4: Restart Backend with SMS OTP

```bash
cd /Users/anilkumarthammineni/Downloads/cafe-webapp/backend
npm run dev
```

When it starts, you should see:
```
[INFO] Fastify listening on 0.0.0.0:4000
```

**NOT:**
```
WARN Twilio is not configured...
```

---

## ✅ Test SMS OTP Flow

### 1️⃣ Start Frontend Server

```bash
cd /Users/anilkumarthammineni/Downloads/cafe-webapp
python3 -m http.server 8000
```

### 2️⃣ Test the OTP Flow

1. Open `http://localhost:8000/index.html`
2. Click **Start Ordering**
3. Select **Table 1**
4. Add items to cart
5. Click **Place Order**
6. In OTP modal, enter your **actual phone number** (with country code)
   - Example: `+919876543210` (India)
   - Example: `+14155552671` (USA)
7. Click **Send OTP**
8. **Wait 10-30 seconds** for SMS to arrive
9. Check your phone for SMS with OTP code
10. Enter OTP and click **Verify & Place Order**

---

## 🔄 OTP Modes

### Mode 1: Demo OTP (Default)
- `OTP_MODE="demo"`
- OTP shown on screen (good for testing without SMS)
- No SMS sent

### Mode 2: Real SMS via Twilio
- `OTP_MODE="sms"`
- Real SMS sent to customer phone
- Also shows OTP on screen as fallback
- Uses Twilio free trial credit

---

## 💡 Twilio Free Trial Limits

- **Free Credit:** $15 (enough for ~150 SMS messages)
- **Max Recipients:** Only verified phone numbers at first
- **Valid For:** 30 days (then upgrade or loses credit)

### Upgrade Later:
After free trial, you pay ~$0.0075 per SMS (very cheap for a cafe)

---

## 🔍 Troubleshooting

### ❌ "SMS not sent" / "Twilio not configured"

**Check:**
```bash
grep TWILIO /Users/anilkumarthammineni/Downloads/cafe-webapp/backend/.env
```

Should show all 3 lines with values (not `[YOUR_...]`)

### ❌ "Authentication failed"

- Double-check Account SID and Auth Token (copy exactly)
- Auth Token is case-sensitive
- Restart backend after changes

### ❌ "Invalid phone number"

- Use international format: `+[country code][number]`
- Examples:
  - India: `+919876543210`
  - USA: `+14155552671`
  - UK: `+441632960000`

### ❌ Still seeing demo OTP, not SMS

- Check `OTP_MODE="sms"` is in `.env`
- Restart backend
- Check Twilio dashboard for message logs

---

## 📊 Monitor SMS in Twilio Console

1. Log into [twilio.com](https://twilio.com)
2. Go to **Messaging** → **Logs**
3. See all SMS sent, delivery status, errors

---

## 🎯 Production Tips

1. **Verify all customer phone numbers** in Twilio console (required for trial)
2. **Upgrade to paid plan** when free trial ends ($0.0075/SMS)
3. **Use short codes** for higher SMS limits (if scaling)
4. **Monitor delivery rates** in Twilio dashboard
5. **Add SMS confirmation** to thank customer

---

## ✨ Next Steps

1. ✅ Create Twilio free trial
2. ✅ Get phone number and credentials
3. ✅ Update `backend/.env`
4. ✅ Restart backend with `npm run dev`
5. ✅ Test SMS flow with your phone
6. ✅ Verify SMS arrives in 10-30 seconds

**Done!** Your cafe now sends real SMS OTP codes! 🎉


