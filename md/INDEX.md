# Sunset Cafe - Complete Documentation

Welcome to **Sunset Cafe**, a full-stack QR-based cafe ordering system with mobile app, web frontend, and backend.

---

## 🚀 Quick Navigation

### For First-Time Setup
1. **[Local Development Setup](./LOCAL_DEVELOPMENT_SETUP.md)** ← Start here
   - Install prerequisites
   - Set up frontend, mobile, backend
   - Run locally for testing

### For Deployment
2. **[Complete Deployment Guide](./COMPLETE_DEPLOYMENT_GUIDE.md)**
   - Deploy to GitHub Pages (Play Site)
   - Deploy to Google Play Store (Mobile)
   - Deploy to Railway (Backend)

### For GitHub Actions
3. **[GitHub Actions Setup](./GITHUB_ACTIONS_SETUP.md)**
   - Configure automated CI/CD
   - Set up secrets
   - Monitor builds

---

## 📚 Documentation Structure

```
md/
├── README.md (← you are here)
├── LOCAL_DEVELOPMENT_SETUP.md      (Local development guide)
├── COMPLETE_DEPLOYMENT_GUIDE.md    (Deploy to production)
├── GITHUB_ACTIONS_SETUP.md         (Automate builds & deployment)
├── START_HERE.md                   (Project overview)
├── LAUNCH_GUIDE.md                 (Go-live checklist)
├── QUICK_REFERENCE.md              (Common commands)
├── TECH_STACK_GUIDE.md             (Technology overview)
├── EMAIL_SETUP_GUIDE.md            (Email configuration)
├── TWILIO_SMS_SETUP.md             (SMS setup)
├── macOS_SETUP.md                  (Mac-specific setup)
└── backend/
    └── README.md                   (Backend details)
└── mobile/
    └── README.md                   (Mobile app details)
```

---

## 🏗️ Architecture Overview

### Three Main Components

```
┌─────────────────────────────────────┐
│      Sunset Cafe System             │
└─────────────────────────────────────┘
              │
    ┌─────────┼─────────┐
    │         │         │
    ▼         ▼         ▼
┌────────┐ ┌────────┐ ┌─────────┐
│Frontend│ │Mobile  │ │ Backend │
│  Web   │ │  App   │ │         │
│        │ │(Android│ │(Spring/ │
│Play    │ │or iOS) │ │ Fastify)│
│Site    │ │        │ │         │
└────────┘ └────────┘ └─────────┘
    │         │         │
    ▼         ▼         ▼
├─────────────┬─────────────┤
│ GitHub Pages│ Google Play │ Railway
│             │ Store       │
└─────────────┴─────────────┘
```

### Component Details

| Component | Location | Tech | Deploy To | CI/CD |
|-----------|----------|------|-----------|-------|
| **Frontend** | `/` + `/play-site` | HTML/CSS/JS | GitHub Pages | ✓ |
| **Mobile** | `/mobile` | Capacitor/Android | Google Play | ✓ |
| **Backend** | `/backend` or `/backend-spring` | Node.js/Spring | Railway | ✓ |

---

## 🎯 Getting Started

### Step 1: Local Development (30 minutes)

```bash
# Follow: LOCAL_DEVELOPMENT_SETUP.md
# Sets up all components locally
# Tests integration
```

### Step 2: Configure GitHub (10 minutes)

```bash
# Follow: GITHUB_ACTIONS_SETUP.md
# Add secrets to GitHub
# Enable workflows
```

### Step 3: Deploy to Production (varies)

```bash
# Follow: COMPLETE_DEPLOYMENT_GUIDE.md
# Deploy Play Site to GitHub Pages
# Deploy Mobile to Google Play
# Deploy Backend to Railway
```

---

## 📋 Pre-Deployment Checklist

- [ ] **Code pushed to GitHub**
  ```bash
  git add .
  git commit -m "Ready for deployment"
  git push origin main
  ```

- [ ] **Environment files created**
  - `backend/.env` or `backend-spring/application-prod.yml`
  - `mobile/www/config.js` with production API URL

- [ ] **Secrets added to GitHub** (GITHUB_ACTIONS_SETUP.md)
  - `ANDROID_KEYSTORE_BASE64`
  - `ANDROID_STORE_PASSWORD`
  - `DATABASE_URL`
  - `JWT_SECRET`
  - etc.

- [ ] **Database ready** (Production PostgreSQL)
  - Created database
  - Schema migrated
  - Backups configured

- [ ] **Play Console account set up**
  - App created
  - Developer account verified
  - Payment method added

- [ ] **Railway account ready**
  - Project created
  - PostgreSQL service added
  - Environment variables configured

---

## 🔑 Key Commands

### Development

```bash
# Start everything locally
./scripts/rebuild-apk.sh http://localhost:3000    # Build mobile
cd backend && npm run dev                          # Start backend
python -m http.server 8000                         # Start frontend
```

### Building

```bash
# Build for production
./scripts/rebuild-apk.sh https://api.railway.app   # Release APK/AAB
cd backend && npm run build && npm start           # Build & run backend
```

### Git Workflow

```bash
# Make changes
git checkout -b feature/your-feature
# ... make changes ...
git add .
git commit -m "Add feature"
git push origin feature/your-feature
# Open PR on GitHub, review, merge to main
# Auto-deploy happens on merge
```

---

## 📊 Deployment Workflow

### Automatic Deployment (On Push to main)

```
Push to main
    ↓
GitHub Actions Triggered
    ├─ Test Frontend (2 min)
    ├─ Build & Test Backend (5 min)
    ├─ Build Android AAB (10 min)
    └─ Deploy Play Site (1 min)
    ↓
Artifacts Ready
    ├─ app-release.aab → Upload to Play Console
    ├─ backend.jar → Deploy to Railway
    └─ play-site/ → Live at github.io
```

### Manual Deployment (When Needed)

```
GitHub Actions Workflow Dispatch
    ↓
Specify Parameters (API URL, etc)
    ↓
Workflow Runs
    ↓
Download Artifacts
    ↓
Manual Upload to Play Store / Railway
```

---

## 🐛 Debugging & Troubleshooting

### Common Issues

| Issue | Solution |
|-------|----------|
| APK build fails | Check Java 17+ installed, API URL valid |
| Backend won't start | Check PostgreSQL running, port free |
| Frontend not loading | Check server running, CORS origin correct |
| Play Site not updating | Check branch settings, wait 1-3 min |
| Mobile app crashes | Check logcat: `adb logcat *:E` |

### Getting Help

1. **Check relevant documentation:**
   - Local dev issues → [LOCAL_DEVELOPMENT_SETUP.md](./LOCAL_DEVELOPMENT_SETUP.md)
   - Deployment issues → [COMPLETE_DEPLOYMENT_GUIDE.md](./COMPLETE_DEPLOYMENT_GUIDE.md)
   - GitHub Actions issues → [GITHUB_ACTIONS_SETUP.md](./GITHUB_ACTIONS_SETUP.md)

2. **View logs:**
   - GitHub Actions: Actions tab → Workflow → Job logs
   - Railway: `railway logs --follow`
   - Local backend: Terminal output
   - Mobile app: `adb logcat`
   - Browser: DevTools (F12)

3. **Open an issue:**
   - Include error message
   - Include logs
   - Include what you were trying to do

---

## 🔐 Security Notes

### Secrets Management

Never commit secrets to Git:

```bash
# ✓ Good: Use GitHub Secrets
git add .gitignore  # Contains *.env, *.key, etc
echo ".env" >> .gitignore

# ✗ Bad: Committing secrets
git add .env  # DON'T DO THIS
```

### API Keys

- Twilio API Key (SMS)
- SendGrid API Key (Email)
- JWT Secret (JWT signing)

All stored in GitHub Secrets, not in code.

### Database Password

- Never hardcoded
- Set via environment variable
- Rotated regularly
- Railway auto-backups enabled

---

## 📈 Scaling & Performance

### Current Architecture

- **Frontend:** Static files on GitHub Pages (highly available)
- **Mobile:** Native Android app on Google Play
- **Backend:** Single container on Railway (auto-scaling available)

### Future Improvements

- Add CDN for static assets
- Database replication for HA
- API rate limiting
- Caching layer (Redis)
- Load balancing

---

## 📞 Support & Communication

### Documentation
- [Startup Guide](./START_HERE.md)
- [Launch Checklist](./LAUNCH_GUIDE.md)
- [Quick Commands](./QUICK_REFERENCE.md)

### Useful Links
- **GitHub:** https://github.com/anilchowdary4531
- **Google Play:** https://play.google.com/console
- **Railway:** https://railway.app
- **GitHub Pages:** https://pages.github.com

---

## 📝 File Organization

```
cafe-webapp/
├── .github/
│   └── workflows/           # GitHub Actions
│       ├── deploy-play-site.yml
│       ├── test-frontend.yml
│       ├── android-build.yml
│       ├── backend-ci.yml
│       └── backend-deploy.yml
├── scripts/
│   ├── rebuild-apk.sh       # Build Android APK
│   └── sync-web-assets.mjs  # Sync assets to mobile
├── mobile/                  # Android mobile app
│   ├── www/                 # Web assets
│   └── android/             # Android native code
├── backend/                 # Fastify Node.js backend
│   └── prisma/              # Database schema
├── backend-spring/          # Spring Boot backend (alternative)
│   └── src/                 # Java source code
├── play-site/               # Marketing website
│   ├── index.html
│   └── privacy-policy.html
├── md/                      # Documentation (YOU ARE HERE)
│   ├── README.md
│   ├── LOCAL_DEVELOPMENT_SETUP.md
│   ├── COMPLETE_DEPLOYMENT_GUIDE.md
│   ├── GITHUB_ACTIONS_SETUP.md
│   └── ...
└── [Root HTML files]        # Main app
    ├── index.html
    ├── staff.html
    └── admin.html
```

---

## 🎓 Learning Resources

### Technology Stack

- **Frontend:** Vanilla JavaScript, HTML5, CSS3
- **Mobile:** Capacitor, Android Gradle
- **Backend:** Node.js (Fastify/Prisma) or Spring Boot/Java
- **Database:** PostgreSQL
- **Deployment:** GitHub Actions, GitHub Pages, Railway, Google Play
- **Version Control:** Git/GitHub

### Recommended Learning Path

1. Read [TECH_STACK_GUIDE.md](./TECH_STACK_GUIDE.md)
2. Complete [LOCAL_DEVELOPMENT_SETUP.md](./LOCAL_DEVELOPMENT_SETUP.md)
3. Set up [GITHUB_ACTIONS_SETUP.md](./GITHUB_ACTIONS_SETUP.md)
4. Follow [COMPLETE_DEPLOYMENT_GUIDE.md](./COMPLETE_DEPLOYMENT_GUIDE.md)
5. Review [LAUNCH_GUIDE.md](./LAUNCH_GUIDE.md)

---

## 🚀 Next Steps

### Immediate (Today)
- [ ] Read [LOCAL_DEVELOPMENT_SETUP.md](./LOCAL_DEVELOPMENT_SETUP.md)
- [ ] Install prerequisites
- [ ] Set up local development environment

### Short-term (This Week)
- [ ] Test all components locally
- [ ] Push code to GitHub
- [ ] Configure GitHub Secrets

### Medium-term (This Month)
- [ ] Deploy Play Site to GitHub Pages
- [ ] Build and upload first APK to Play Console
- [ ] Deploy backend to Railway
- [ ] Test end-to-end flow

### Long-term (Ongoing)
- [ ] Monitor GitHub Actions
- [ ] Set up monitoring & alerts
- [ ] Plan feature development
- [ ] Gather user feedback

---

## 💡 Tips

1. **Use feature branches:**
   ```bash
   git checkout -b feature/new-menu-items
   # ... make changes ...
   git push origin feature/new-menu-items
   # Open PR, review, merge to main
   ```

2. **Keep secrets secure:**
   - Never log secrets in CI/CD
   - Use GitHub Secrets, not .env files
   - Rotate regularly

3. **Monitor deployments:**
   - GitHub Actions tab shows all builds
   - Set up email notifications
   - Review logs before merging

4. **Test locally first:**
   ```bash
   ./scripts/rebuild-apk.sh http://localhost:3000
   # Test before pushing
   ```

5. **Document changes:**
   - Clear commit messages
   - Update docs when architecture changes
   - Leave comments for complex code

---

## ❓ FAQ

**Q: How often are builds triggered?**
A: On every push to `main`. Set branch protection to require passing builds.

**Q: Can I skip a deployment?**
A: Yes, add `[skip ci]` to commit message: `git commit -m "Fix typo [skip ci]"`

**Q: How do I rollback a deployment?**
A: 
- Git: `git revert COMMIT_HASH && git push`
- Railway: Redeploy previous version from console
- Play Store: Upload previous APK version

**Q: How do I test before merging to main?**
A: Use feature branches and Pull Requests with branch protection.

**Q: Where are my secrets stored?**
A: GitHub Secrets (encrypted). Never visible in logs. See [GITHUB_ACTIONS_SETUP.md](./GITHUB_ACTIONS_SETUP.md).

**Q: How do I update the database schema?**
A: 
- Fastify: Create Prisma migration with `npm run prisma:migrate`
- Spring: Add Flyway migration in `src/main/resources/db/migration/`

---

## 📞 Contact & Support

For issues, questions, or contributions:

1. Check relevant documentation above
2. Search existing GitHub issues
3. Open new GitHub issue with:
   - Clear description
   - Error messages/logs
   - Steps to reproduce
   - Your environment (OS, Node version, Java version, etc)

---

## 📄 License

This project is part of Sunset Cafe. See individual component READMEs for license details.

---

## 🎉 Ready to Start?

### **→ [Begin with LOCAL_DEVELOPMENT_SETUP.md](./LOCAL_DEVELOPMENT_SETUP.md)**

This will guide you through:
- Installing all prerequisites
- Setting up each component
- Running locally
- Testing integration

Good luck! 🚀

