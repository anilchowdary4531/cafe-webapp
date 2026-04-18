# Railway Deployment Safety Guide

## 🚨 Will Railway Create Duplicate Projects/Services?

**NO!** Railway is designed to be **safe and idempotent**. Here's exactly how it handles existing deployments:

## 🔍 Railway's Smart Deployment Logic

### Project Level
```
First Time: Creates new Railway project from GitHub repo
Subsequent: Uses existing linked project (no duplicates)
```

### Service Level
```
Existing Service: Updates with new code (rolling deployment)
New Service: Creates only if defined in railway.toml but doesn't exist
```

## 🛡️ Safety Features

### 1. **GitHub Repository Linking**
- One Railway project per GitHub repository
- Automatic linking prevents duplicate projects
- If repo is already linked, Railway uses existing project

### 2. **Service Detection**
- Reads `railway.toml` for service definitions
- Matches services by name (`cafe-backend`, `cafe-spring-backend`)
- Updates existing services, creates missing ones

### 3. **Database Protection**
- PostgreSQL database is **never recreated**
- Existing data is **always preserved**
- Only schema updates happen (via your migrations)

### 4. **Environment Variables**
- Existing variables are **preserved**
- New variables from `railway.toml` are **added**
- Manual variables in dashboard are **never overwritten**

## 📋 Deployment Scenarios

### Scenario 1: First Deployment
```
GitHub Push → Railway detects new repo → Creates project → Creates services → Sets up database
```

### Scenario 2: Code Updates
```
GitHub Push → Railway finds existing project → Updates services → Preserves database → Zero downtime
```

### Scenario 3: Adding New Service
```
Add service to railway.toml → GitHub Push → Railway creates new service → Integrates with existing project
```

### Scenario 4: Configuration Changes
```
Update railway.toml → GitHub Push → Railway applies new config → Updates existing services
```

## 🔒 What Railway NEVER Does

❌ **Never deletes existing services**  
❌ **Never recreates databases**  
❌ **Never overwrites manual environment variables**  
❌ **Never creates duplicate projects**  
❌ **Never loses your data**

## 🏗️ Safe Development Workflow

### For Development
```bash
# Safe to push frequently
git add .
git commit -m "feature: add new endpoint"
git push origin main
# Railway: Updates existing services safely
```

### For Production
```bash
# Same safety applies
git checkout main
git merge feature-branch
git push origin main
# Railway: Rolling deployment, zero downtime
```

## 🚨 Emergency Stop

If you ever need to stop automatic deployments:

1. **Pause in Railway Dashboard:**
   - Go to Project Settings
   - Toggle "Auto Deploy" off

2. **Remove GitHub Integration:**
   - Project Settings → GitHub
   - Disconnect repository

3. **Manual Deployments Only:**
   - Use Railway dashboard "Deploy" button
   - Or Railway CLI: `railway up`

## 🔍 Verification Commands

Check your Railway setup:

```bash
# Via Railway CLI (if installed)
railway status
railway services

# Via Dashboard
# https://railway.app/dashboard → Your Project
```

## 💡 Best Practices

### 1. **Test Locally First**
```bash
# Always test before pushing
./deployment/scripts/build.sh all
cd deployment/docker && docker-compose up -d
```

### 2. **Use Feature Branches**
```bash
git checkout -b feature/new-endpoint
# Develop and test
git checkout main
git merge feature/new-endpoint
git push origin main  # Safe deployment
```

### 3. **Monitor Deployments**
- Watch Railway dashboard during first few deployments
- Set up deployment notifications
- Check logs for any issues

### 4. **Backup Strategy**
- Railway auto-backups databases
- Export important data before major changes
- Test restore procedures

## 🚀 Confidence Building

Railway's architecture ensures:
- **Idempotent operations** (safe to run multiple times)
- **Transactional deployments** (all or nothing)
- **Automatic rollbacks** on failure
- **Data preservation** guaranteed

**Bottom Line:** You can push to main branch with confidence - Railway will never break your existing setup!

## 📞 Support

If concerned about specific scenarios:
- Check Railway status: https://railway.app/dashboard
- Review deployment logs in Railway dashboard
- Contact Railway support for edge cases
