# Railway Deployment Guide

Railway offers multiple ways to deploy your cafe-webapp automatically. Here are the recommended approaches:

## 🚂 Method 1: GitHub Integration (Recommended)

Railway can automatically deploy from your GitHub repository:

### Setup Steps:

1. **Connect GitHub Repository:**
   - Go to Railway dashboard
   - Click "New Project" → "Deploy from GitHub repo"
   - Select your cafe-webapp repository

2. **Configure Services:**
   - Railway will auto-detect your backends
   - Set environment variables in Railway dashboard
   - Configure domains and networking

3. **Automatic Deployments:**
   - Every push to main/master triggers deployment
   - No CI/CD configuration needed for basic setup

## 🔧 Method 2: Railway CLI + GitHub Actions

For more control, use the Railway CLI in your CI/CD pipeline:

### Prerequisites:
- Railway account and project
- `RAILWAY_TOKEN` secret in GitHub

### Railway CLI Commands:
```bash
# Install CLI
curl -fsSL https://railway.app/install.sh | sh

# Login
railway login --token $RAILWAY_TOKEN

# Deploy specific service
railway up --service cafe-backend

# Check status
railway status
```

## 🌐 Method 3: Railway API Webhooks

For advanced automation:

### Webhook Configuration:
```bash
# Railway can send webhooks on deployment events
# Configure in Railway dashboard → Project Settings → Webhooks
```

## 📋 Railway Project Structure

### Recommended Service Setup:

```
cafe-webapp (Railway Project)
├── cafe-backend (Node.js API)
│   ├── Source: backend/
│   ├── Build Command: npm run build
│   ├── Start Command: npm start
│   └── Port: 3000
├── cafe-spring-backend (Spring Boot API)
│   ├── Source: backend-spring/
│   ├── Build Command: ./mvnw package
│   ├── Start Command: java -jar target/*.jar
│   └── Port: 4100
└── cafe-database (PostgreSQL)
    ├── Managed by Railway
    └── Auto-provisioned
```

## ⚙️ Environment Variables

Set these in Railway dashboard:

```bash
# Database
DATABASE_URL=postgresql://...

# JWT
JWT_SECRET=your-secret

# Email/SMS (optional)
EMAIL_USER=...
EMAIL_PASS=...
TWILIO_ACCOUNT_SID=...
TWILIO_AUTH_TOKEN=...
```

## 🔄 Deployment Flow

```
Git Push → GitHub Actions → Tests Pass → Railway Deploy → Live Update
    ↓              ↓              ↓              ↓              ↓
 main branch → CI Pipeline → Build Images → Railway API → Zero-downtime
```

## 💰 Cost Optimization

Railway Pricing (as of 2024):
- **Hobby Plan:** $5/month (perfect for development)
- **Pro Plan:** $10/month (recommended for production)
- **Team Plan:** $20/month (multiple services)

**Why Railway saves money:**
- No server management costs
- Automatic scaling
- Pay only for actual usage
- Built-in database and monitoring

## 🚀 Quick Start with Railway

1. **Create Railway Account:** https://railway.app
2. **Connect GitHub:** Authorize Railway to access your repo
3. **Deploy:** Railway auto-detects and deploys your services
4. **Configure:** Set environment variables and domains
5. **Scale:** Upgrade plan as needed

## 🔍 Monitoring & Logs

Railway provides:
- Real-time logs
- Performance metrics
- Database monitoring
- Deployment history
- Rollback capabilities

## 🆘 Troubleshooting

### Common Issues:

1. **Build Failures:**
   - Check Railway build logs
   - Ensure all dependencies are in package.json/pom.xml

2. **Environment Variables:**
   - Verify all required env vars are set
   - Check variable names match your code

3. **Database Connection:**
   - Use Railway's built-in PostgreSQL
   - Update DATABASE_URL in environment

4. **Port Configuration:**
   - Ensure services listen on correct ports
   - Configure port mapping in Railway

### Support:
- Railway Docs: https://docs.railway.app/
- Community: Railway Discord
- Support: Railway dashboard → Help

## 🔐 Security

Railway automatically provides:
- SSL/TLS certificates
- DDoS protection
- Secure environment variables
- Network isolation

This setup gives you production-ready deployment with minimal configuration!
