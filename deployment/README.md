# Cafe Webapp Deployment

This directory contains the deployment configuration and scripts for the Cafe Webapp, supporting multiple platforms: Web, Mobile (Android/iOS), and Desktop (Windows/macOS/Linux).

## Project Structure

```
deployment/
├── docker/                 # Docker configurations
│   ├── docker-compose.yml  # Local development orchestration
│   └── nginx.conf         # Nginx reverse proxy config
├── k8s/                   # Kubernetes manifests
│   ├── backend-node-deployment.yaml
│   ├── backend-spring-deployment.yaml
│   ├── postgres-statefulset.yaml
│   ├── hpa.yaml           # Horizontal Pod Autoscaling
│   └── secrets.yaml       # Secrets template
├── scripts/               # Build and deployment scripts
│   └── build.sh           # Multi-platform build script
├── ci-cd/                 # CI/CD pipeline configurations
└── .env.example           # Environment variables template
```

## Supported Platforms

### 🌐 Web Application
- **Frontend**: Vanilla JavaScript SPA
- **Backend**: Node.js (Fastify) + Spring Boot microservices
- **Database**: PostgreSQL
- **Deployment**: Docker + Kubernetes / Railway

### 📱 Mobile Application
- **Framework**: Capacitor (Cordova/PhoneGap)
- **Platforms**: Android (APK/AAB), iOS (IPA)
- **Build Tools**: Gradle (Android), Xcode (iOS)

### 💻 Desktop Application
- **Framework**: Electron
- **Platforms**: Windows (EXE), macOS (DMG), Linux (AppImage)
- **Packaging**: electron-builder

## Quick Start

### Prerequisites

- Docker & Docker Compose
- Node.js 18+
- Java 17+ (for mobile builds)
- kubectl (for Kubernetes deployments)
- Android Studio (for Android builds)
- Xcode (for iOS builds, macOS only)

### 1. Environment Setup

```bash
# Copy environment template
cp deployment/.env.example .env

# Edit with your actual values
nano .env
```

### 2. Build All Platforms

```bash
# Build everything
./deployment/scripts/build.sh all

# Or build specific platforms
./deployment/scripts/build.sh web desktop mobile backends
```

### 3. Local Development

```bash
# Start all services locally
cd deployment/docker
docker-compose up -d

# Access the application
# Web: http://localhost
# API: http://localhost/api/
# Admin API: http://localhost/admin-api/
```

## Platform-Specific Deployment

### Web Deployment

#### Option A: Docker Compose (Development/Local)

```bash
cd deployment/docker
docker-compose up -d
```

#### Option B: Kubernetes (Production)

```bash
# Apply Kubernetes manifests
kubectl apply -f deployment/k8s/

# Apply secrets (after filling in actual values)
kubectl apply -f deployment/k8s/secrets.yaml
```

#### Option C: Railway (Simple Cloud)

The CI/CD pipeline automatically deploys to Railway on main branch pushes.

### Mobile Deployment

#### Android

```bash
cd mobile
npx cap build android
cd android
./gradlew assembleDebug  # For debug APK
./gradlew bundleRelease  # For release AAB
```

#### iOS (macOS only)

```bash
cd mobile
npx cap build ios
cd ios/App
xcodebuild -workspace App.xcworkspace -scheme App -configuration Release archive
```

### Desktop Deployment

```bash
cd desktop
npm install
npm run build:win    # Windows
npm run build:mac    # macOS
npm run build:linux  # Linux
```

## CI/CD Pipelines

GitHub Actions workflows are configured for automated deployment:

- **deploy-web.yml**: Tests and deploys web application
- **deploy-mobile.yml**: Builds Android/iOS apps on releases
- **deploy-desktop.yml**: Builds desktop apps for all platforms

### Required Secrets

Add these to your GitHub repository secrets:

```
RAILWAY_TOKEN
AWS_ACCESS_KEY_ID
AWS_SECRET_ACCESS_KEY
AWS_REGION
SIGNING_KEY_ALIAS
SIGNING_KEY_PASSWORD
SIGNING_STORE_PASSWORD
```

## Infrastructure Scaling

### Kubernetes Autoscaling

Horizontal Pod Autoscalers are configured for:
- CPU utilization > 70%
- Memory utilization > 80%

### Database Scaling

- PostgreSQL StatefulSet with persistent storage
- Connection pooling recommended for high traffic
- Read replicas can be added for read-heavy workloads

## Security Considerations

### Secrets Management

- Use Kubernetes secrets or cloud secret managers
- Never commit sensitive data to version control
- Rotate secrets regularly

### HTTPS/TLS

- Configure SSL certificates in production
- Use cert-manager for automatic certificate management in Kubernetes

### Network Security

- Restrict database access to application pods only
- Use network policies in Kubernetes
- Implement proper CORS policies

## Monitoring & Observability

### Recommended Tools

- **Prometheus**: Metrics collection
- **Grafana**: Dashboard visualization
- **ELK Stack**: Log aggregation and analysis
- **Jaeger**: Distributed tracing

### Health Checks

- Backend services include health check endpoints
- Kubernetes liveness and readiness probes configured
- Docker health checks included in Dockerfiles

## Troubleshooting

### Common Issues

1. **Port conflicts**: Ensure ports 3000, 4100, 5432 are available
2. **Database connection**: Verify DATABASE_URL and credentials
3. **Mobile builds**: Ensure Android SDK and Xcode are properly configured
4. **Desktop builds**: Check electron and electron-builder versions

### Logs

```bash
# Docker Compose logs
docker-compose logs -f

# Kubernetes logs
kubectl logs -f deployment/cafe-backend-node
kubectl logs -f deployment/cafe-backend-spring

# Application logs
kubectl logs -f -l app=cafe-backend-node
```

## Contributing

1. Test builds locally before pushing
2. Update documentation for infrastructure changes
3. Use semantic versioning for releases
4. Tag releases to trigger automated builds

## Support

For deployment issues:
1. Check the troubleshooting section
2. Review CI/CD pipeline logs
3. Verify environment variables
4. Check network connectivity and firewall rules

## License

This deployment configuration is part of the Cafe Webapp project.
