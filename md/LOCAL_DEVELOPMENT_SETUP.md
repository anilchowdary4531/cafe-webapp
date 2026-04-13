# Local Development Setup Guide

Complete guide for setting up Sunset Cafe for local development on macOS.

---

## Prerequisites

### System Requirements
- macOS 12+
- 16GB RAM minimum
- 50GB disk space

### Required Software

| Tool | Version | Install |
|------|---------|---------|
| Node.js | 20+ | `brew install node` |
| Java | 17+ | https://adoptium.net/ |
| Android Studio | Latest | https://developer.android.com/studio |
| Git | Latest | `brew install git` |
| Xcode | 15+ | `xcode-select --install` |

**Check versions:**
```bash
node --version     # v20.x.x
npm --version      # 10.x.x
java -version      # java 17+ 
```

---

## Part 1: Frontend Setup

### 1a. Root Frontend (Main Web App)

```bash
# Navigate to project
cd /Users/anilkumarthammineni/Downloads/cafe-webapp

# Install dependencies (if any Node deps)
npm install

# Serve locally
python -m http.server 8000

# Open in browser
open http://localhost:8000
```

### 1b. Play Site (Marketing/Landing)

```bash
cd play-site

# Serve locally
python -m http.server 8001

# Open in browser
open http://localhost:8001
```

### Testing Frontend

```bash
# Test HTML files
# Use browser DevTools (F12 or Cmd+Option+I)

# Clear cache before testing changes
# Browser menu → Settings → Clear browsing data

# Or use Private/Incognito mode
```

---

## Part 2: Mobile App Setup

### 2a. Install Capacitor & Dependencies

```bash
cd mobile

# Install Node modules
npm install

# Install Capacitor CLI globally (optional but recommended)
npm install -g @capacitor/cli

# Generate Capacitor config
npm run cap:sync
```

### 2b. Android Configuration

#### Set Up Android SDK

```bash
# Download Android Studio from:
# https://developer.android.com/studio

# After installation, set ANDROID_HOME:
echo 'export ANDROID_HOME=~/Library/Android/sdk' >> ~/.zshrc
source ~/.zshrc

# Verify
echo $ANDROID_HOME
# Should output: /Users/YOUR_USERNAME/Library/Android/sdk
```

#### Accept Android Licenses

```bash
flutter doctor --android-licenses
# Accept all licenses (press 'y' for each)

# Or manually:
~/Library/Android/sdk/cmdline-tools/latest/bin/sdkmanager --licenses
```

#### Configure signing.properties

```bash
cd mobile

# Copy example (if it exists)
cp signing.properties.example signing.properties

# Edit with your keystore details
nano signing.properties
```

Add:
```properties
storeFile=sunset-cafe-upload-key.jks
storePassword=YOUR_KEYSTORE_PASSWORD
keyAlias=sunset-cafe
keyPassword=YOUR_KEY_PASSWORD
```

Or generate new keystore:

```bash
keytool -genkey -v -keystore sunset-cafe-upload-key.jks \
  -keyalg RSA -keysize 2048 -validity 36500 \
  -alias sunset-cafe
```

### 2c. Build & Run

#### Sync Web Assets

```bash
cd mobile

# Sync frontend with mobile
npm run cap:sync

# Or with API URL
MOBILE_API_BASE_URL=http://localhost:8080 npm run cap:sync
```

#### Build APK (Debug)

```bash
cd android
./gradlew assembleDebug

# Output: app/build/outputs/apk/debug/app-debug.apk
```

#### Build APK (Release)

```bash
# From mobile directory
./scripts/rebuild-apk.sh http://localhost:8080

# Output: android/app/build/outputs/apk/release/app-release.apk
```

#### Install on Device/Emulator

```bash
# With USB connected device:
adb install -r mobile/android/app/build/outputs/apk/debug/app-debug.apk

# Launch app
adb shell am start -n com.sunsetcafe.qrapp/.MainActivity

# View logs
adb logcat
```

#### Open in Android Studio

```bash
cd mobile/android

# Open in Android Studio
open -a "Android Studio" .

# Or via command line
studio .
```

### 2d. Emulator Setup

```bash
# Create virtual device
~/Library/Android/sdk/emulator/emulator -list-avds
# (If needed, create in Android Studio: Tools → Device Manager)

# Launch emulator
~/Library/Android/sdk/emulator/emulator -avd DEVICE_NAME

# Install APK to emulator
adb install -r mobile/android/app/build/outputs/apk/debug/app-debug.apk
```

---

## Part 3: Backend Setup

### 3a. Node.js Backend (Fastify)

```bash
cd backend

# Install dependencies
npm install

# Create .env file
cat > .env << 'EOF'
DATABASE_URL="postgresql://postgres:password@localhost:5432/cafe_webapp"
JWT_SECRET="your-secret-minimum-32-characters-long"
CORS_ORIGIN="http://localhost:8000"
OTP_PROVIDER="demo"
TWILIO_ACCOUNT_SID="your-twilio-sid"
TWILIO_AUTH_TOKEN="your-twilio-token"
TWILIO_PHONE_NUMBER="+1234567890"
SENDGRID_API_KEY="your-sendgrid-key"
ADMIN_EMAIL="admin@cafe.local"
EOF

# Start development server
npm run dev
# Server runs at: http://localhost:3000

# Or start production
npm run build
npm start
```

### 3b. Spring Boot Backend (Java)

```bash
cd backend-spring

# View README for full setup
cat README.md

# Install dependencies
mvn clean install

# Create application.yml with environment
cat > src/main/resources/application-dev.yml << 'EOF'
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/cafe_webapp
    username: postgres
    password: password
  jpa:
    hibernate:
      ddl-auto: update
  flyway:
    enabled: false

app:
  jwt:
    secret: your-secret-minimum-32-characters-long
  cors-origin: http://localhost:8000
  otp:
    provider: demo

logging:
  level:
    root: INFO
    com.cafe: DEBUG
EOF

# Run with profile
mvn spring-boot:run -Dspring-boot.run.arguments="--spring.profiles.active=dev"

# Or build & run
mvn clean package
java -jar target/backend-spring-*.jar --spring.profiles.active=dev
```

### 3c. Database Setup

#### PostgreSQL (Local)

```bash
# Install PostgreSQL
brew install postgresql

# Start service
brew services start postgresql

# Create database
createdb cafe_webapp

# Create user (if needed)
createuser -P cafe_user
# When prompted, set password

# Connect to verify
psql -U postgres -d cafe_webapp

# Exit
\q
```

#### Using Docker

```bash
# Start PostgreSQL in Docker
docker run -d \
  --name postgres-cafe \
  -e POSTGRES_DB=cafe_webapp \
  -e POSTGRES_PASSWORD=password \
  -p 5432:5432 \
  postgres:15

# View logs
docker logs postgres-cafe

# Stop
docker stop postgres-cafe
```

### 3d. Running Migrations

#### Fastify with Prisma

```bash
cd backend

# Generate Prisma client
npm run prisma:generate

# Run migrations
npm run prisma:migrate

# Seed database (optional)
npm run prisma:seed
```

#### Spring Boot with Flyway

Migrations run automatically on startup.

Check `backend-spring/src/main/resources/db/migration/`

---

## Part 4: Full Stack Integration

### Testing Complete Flow

1. **Start Backend:**
   ```bash
   cd backend
   npm run dev
   # Or: cd backend-spring && mvn spring-boot:run
   ```

2. **Start Frontend:**
   ```bash
   python -m http.server 8000
   # In new terminal from cafe-webapp root
   ```

3. **Update Config:**
   ```bash
   # Edit mobile/www/config.js or root config.js
   # Set API_URL to http://localhost:3000 (or 8080 for Spring)
   ```

4. **Sync Mobile:**
   ```bash
   cd mobile
   MOBILE_API_BASE_URL=http://localhost:3000 npm run cap:sync
   ```

5. **Build Mobile:**
   ```bash
   ./scripts/rebuild-apk.sh http://localhost:3000
   ```

6. **Test on Device:**
   ```bash
   adb install -r mobile/android/app/build/outputs/apk/debug/app-debug.apk
   ```

7. **Monitor Logs:**
   - Backend logs: Terminal
   - Mobile logs: `adb logcat`
   - Browser console: F12

---

## Development Workflow

### Making Frontend Changes

```bash
# 1. Edit HTML/JS/CSS files
nano index.html

# 2. Reload browser (Cmd+R or click Reload)

# 3. For mobile, sync changes
cd mobile
npm run cap:sync

# 4. Rebuild APK
./scripts/rebuild-apk.sh http://localhost:3000

# 5. Reinstall on device
adb install -r mobile/android/app/build/outputs/apk/debug/app-debug.apk
```

### Making Backend Changes

```bash
# 1. Edit Spring Boot code
nano backend-spring/src/main/java/com/cafe/service/UserService.java

# 2. With auto-reload enabled, changes picked up automatically
# Or restart server (Ctrl+C and npm run dev)

# 3. Test endpoints
curl http://localhost:8080/health

# 4. Check logs for errors
```

### Making Database Changes

```bash
# For Prisma (Fastify backend):
# 1. Edit backend/prisma/schema.prisma
# 2. Create migration
npm run prisma:migrate

# For Flyway (Spring backend):
# 1. Create new migration file in backend-spring/src/main/resources/db/migration/
# 2. Name format: V{VERSION}__{DESCRIPTION}.sql
# 3. Run on startup automatically
```

---

## Useful Commands

### Frontend
```bash
# Open in browser
open http://localhost:8000

# Clear cache (avoid)
rm -rf ~/.cache/chromium
```

### Mobile
```bash
# List connected devices
adb devices

# View logs
adb logcat | grep cafe

# Install app
adb install -r app-debug.apk

# Uninstall app
adb uninstall com.sunsetcafe.qrapp

# Launch app
adb shell am start -n com.sunsetcafe.qrapp/.MainActivity

# Clear app data
adb shell pm clear com.sunsetcafe.qrapp
```

### Backend (Fastify)
```bash
# Install node_modules
npm ci

# Generate Prisma types
npm run prisma:generate

# Run tests
npm test

# Format code
npm run format
```

### Backend (Spring)
```bash
# Clean build
mvn clean install

# Run tests
mvn test

# Build JAR
mvn package

# View dependencies
mvn dependency:tree

# Format code
mvn spring-javaformat:apply
```

### Database (PostgreSQL)
```bash
# Connect to database
psql -U postgres -d cafe_webapp

# List databases
\l

# List tables
\dt

# View table schema
\d table_name

# Exit
\q

# Backup database
pg_dump -U postgres cafe_webapp > backup.sql

# Restore database
psql -U postgres cafe_webapp < backup.sql
```

---

## Debugging

### Mobile App Crashes

```bash
# View crash logs
adb logcat | grep FATAL

# Uninstall and reinstall
adb uninstall com.sunsetcafe.qrapp
adb install -r app-debug.apk

# Check logcat for errors
adb logcat *:E
```

### Backend Not Starting

```bash
# Check if port is in use
lsof -i :3000
lsof -i :8080

# Kill process
kill -9 <PID>

# Check logs
npm run dev 2>&1 | tee debug.log
```

### Database Connection Failed

```bash
# Check PostgreSQL running
brew services list | grep postgres

# Check connection
psql -U postgres -d cafe_webapp

# View logs
tail -f /usr/local/var/log/postgres.log
```

### Frontend Not Loading

```bash
# Check server running
curl http://localhost:8000

# Check for CORS errors
# Open DevTools (F12) → Console tab

# Check file paths
ls -la *.html *.js *.css

# Clear browser cache
# Settings → Clear browsing data
```

---

## Tips & Tricks

1. **Use source maps for debugging:**
   ```bash
   # Frontend: Enable DevTools
   # Mobile: adb logcat shows errors
   # Backend: npm run dev shows all logs
   ```

2. **Hot reload:**
   - Frontend: Browser refresh (Cmd+R)
   - Backend: npm run dev auto-reloads on file change
   - Mobile: Rebuild with npm run cap:sync

3. **Mock API responses:**
   - Edit config.js to use local JSON files
   - Or mock server with `json-server`

4. **Performance profiling:**
   - Frontend: DevTools → Performance tab
   - Backend: Check response times in logs
   - Mobile: Android Studio Profiler

5. **Network inspection:**
   - Frontend: DevTools → Network tab
   - Mobile: adb logcat filter
   - Backend: Spring Boot actuator endpoints

---

## Troubleshooting

### Common Issues

**Issue: Android SDK not found**
```bash
Solution: 
export ANDROID_HOME=~/Library/Android/sdk
export PATH=$ANDROID_HOME/platform-tools:$PATH
```

**Issue: Java version mismatch**
```bash
Solution:
/usr/libexec/java_home -v 17  # Check where Java 17+ is
export JAVA_HOME=$(/usr/libexec/java_home -v 17)
```

**Issue: npm dependencies conflict**
```bash
Solution:
rm -rf node_modules package-lock.json
npm install
```

**Issue: Port already in use**
```bash
Solution:
# Find and kill process
lsof -i :3000
kill -9 <PID>

# Or change port
npm run dev -- --port 3001
```

---

## Next Steps

1. Complete this setup
2. Run full stack locally
3. Make a test change and verify all components update
4. Review [GITHUB_ACTIONS_SETUP.md](./GITHUB_ACTIONS_SETUP.md)
5. Set up GitHub Actions for CI/CD
6. Deploy to production using [COMPLETE_DEPLOYMENT_GUIDE.md](./COMPLETE_DEPLOYMENT_GUIDE.md)

---

## Support

- Check individual component READMEs:
  - `md/backend/README.md`
  - `md/mobile/README.md`
  - `play-site/DEPLOY.md`

- Check error logs:
  - Terminal output
  - Browser DevTools (F12)
  - Android Studio Logcat
  - `adb logcat`

- Ask questions:
  - Open GitHub issue
  - Include error messages and logs

