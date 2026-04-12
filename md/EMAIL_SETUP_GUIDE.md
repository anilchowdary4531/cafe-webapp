# Email Notifications Setup Guide

This guide walks you through setting up SMTP for order completion emails.

## 🎯 What happens

When an order is:
1. ✅ Marked as **Served** (status change)
2. ✅ Payment marked as **Paid** (payment change)

**→ Customer receives a detailed email with:**
- Order ID and table number
- Itemized list of items ordered
- Payment breakdown (subtotal, tax, total)
- Thank you message

---

## 📧 Email Provider Options

### Option 1: Gmail (Free, Easiest)

1. Go to [Google Account Security](https://myaccount.google.com/security)
2. Enable **2-Step Verification**
3. Create an **App Password**:
   - Select **Mail** → **Windows Computer** (or your device)
   - Copy the 16-character password

4. Update `backend/.env`:
```dotenv
SMTP_HOST="smtp.gmail.com"
SMTP_PORT=587
SMTP_USER="your-email@gmail.com"
SMTP_PASS="xxxx xxxx xxxx xxxx"
EMAIL_FROM="Sunset Cafe <your-email@gmail.com>"
```

### Option 2: SendGrid (More Reliable)

1. Sign up at [SendGrid.com](https://sendgrid.com)
2. Create an API key
3. Update `backend/.env`:
```dotenv
SMTP_HOST="smtp.sendgrid.net"
SMTP_PORT=587
SMTP_USER="apikey"
SMTP_PASS="SG.your-api-key-here"
EMAIL_FROM="Sunset Cafe <noreply@sunsetcafe.com>"
```

### Option 3: Mailtrap (For Testing)

1. Sign up at [Mailtrap.io](https://mailtrap.io) (free tier available)
2. Go to **SMTP Settings** and copy credentials
3. Update `backend/.env`:
```dotenv
SMTP_HOST="smtp.mailtrap.io"
SMTP_PORT=587
SMTP_USER="your-username"
SMTP_PASS="your-password"
EMAIL_FROM="Sunset Cafe <demo@sunsetcafe.com>"
```

---

## 🚀 Complete Setup Steps

### Step 1: Update Backend Configuration

```bash
# Edit the .env file with your SMTP credentials
nano /Users/anilkumarthammineni/Downloads/cafe-webapp/backend/.env
```

Ensure these variables are set:
```dotenv
DATABASE_URL="postgresql://..."
PORT=4000
CORS_ORIGIN="http://localhost:8000,http://localhost,capacitor://localhost"
SMTP_HOST="your-smtp-host"
SMTP_PORT=587
SMTP_USER="your-email"
SMTP_PASS="your-password"
EMAIL_FROM="Sunset Cafe <no-reply@sunsetcafe.com>"
```

### Step 2: Run Database Migration

```bash
cd /Users/anilkumarthammineni/Downloads/cafe-webapp/backend
npm run prisma:migrate -- --name order_payment_email_notifications
npm run prisma:generate
```

### Step 3: Start Backend Server

```bash
npm run dev
```

You should see:
```
[INFO] Fastify listening on 0.0.0.0:4000
```

### Step 4: Start Frontend Server

In a **new terminal**:
```bash
cd /Users/anilkumarthammineni/Downloads/cafe-webapp
python3 -m http.server 8000
```

---

## ✅ Test Email Flow (Step-by-Step)

### 1️⃣ Set up customer profile with email

1. Open `http://localhost:8000/index.html` in browser
2. Click **Profile** button (top-right)
3. Enter:
   - **Display Name**: "Test Customer"
   - **Phone**: "9876543210"
   - **Email**: "your-test-email@gmail.com"
4. Toggle **Order status notifications**: ON
5. Click **Save Settings**

### 2️⃣ Create and place an order

1. Click **Start Ordering**
2. Select **Table 1**
3. Add items to cart:
   - 2x Cappuccino
   - 1x Veg Sandwich
4. Click **Place Order**
5. Enter phone and verify OTP
6. Confirm order placed

### 3️⃣ Mark order as Served

1. Open `http://localhost:8000/staff.html`
2. Find the order from Table 1
3. Click **Served** button

### 4️⃣ Mark payment as Paid

1. Same order card
2. Click **Mark Paid** button

### 5️⃣ Check your email inbox

**Wait 5-30 seconds** (depending on provider)

You should receive an email with:

```
Subject: Order Confirmed & Paid - Sunset Cafe Order #clq7x9...

ORDER DETAILS
─────────────────
Order ID: #clq7x9...
Table: Table 1
Items: 2x Cappuccino, 1x Veg Sandwich

PAYMENT SUMMARY
───────────────
Subtotal: $8.00
Tax (5%): $0.40
Total: $8.40
Payment Status: PAID ✓

Thank you for ordering at Sunset Cafe!
```

---

## 🔍 Troubleshooting

### ❌ "SMTP is not configured" warning

**Solution:** Check `backend/.env` has all SMTP variables:
```bash
grep SMTP /Users/anilkumarthammineni/Downloads/cafe-webapp/backend/.env
```

### ❌ Email not received after 1 minute

**Check backend logs:**
- Look for `"Failed to send completion email"` errors
- Verify SMTP credentials are correct
- Check spam/junk folder

### ❌ "Authentication failed" error

**Common causes:**
- Gmail: App Password not created (need 2FA first)
- SendGrid: Wrong API key format
- Mailtrap: Credentials copied incorrectly

**Fix:** Regenerate credentials and update `.env`, restart backend

### ❌ SMTP timeout

**Solution:** 
- Check internet connection
- Try different SMTP port (587 vs 465)
- Use a different email provider

---

## 📧 Email Templates Sent

### Completion Email (Order + Payment)
- **Trigger:** Status = `served` AND Payment = `paid`
- **To:** Customer email (from profile)
- **Format:** Text + HTML
- **Sent:** Once per order (tracked to prevent duplicates)

---

## 🎓 What Gets Stored

After email is sent successfully:
- `completionEmailSentAt` timestamp is recorded
- Prevents duplicate emails if button clicked twice

Query database to verify:
```bash
psql cafe_webapp -c "SELECT id, completionEmailSentAt FROM \"Order\" LIMIT 5;"
```

---

## 📝 Next Steps

Once email setup is working:

1. ✅ Test with multiple orders
2. ✅ Test with different tables
3. ✅ Verify email content matches expectations
4. ✅ Check spam folder handling
5. ✅ Deploy to production with production SMTP

---

## 💡 Pro Tips

- **Gmail:** Create a separate email account just for your cafe to avoid 2FA issues
- **SendGrid/Mailgun:** Free tiers include 100-1000 emails/month
- **Testing:** Use Mailtrap first to see email without sending to real inbox
- **Production:** Use a dedicated email service (SendGrid, AWS SES) for better deliverability


