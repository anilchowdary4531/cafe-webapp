#!/bin/bash

# Cafe Webapp Deployment Build Script
# This script prepares assets for different deployment targets

set -e

echo "🚀 Starting Cafe Webapp Deployment Build"

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# Project root
PROJECT_ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/../.." && pwd)"
DEPLOYMENT_DIR="$PROJECT_ROOT/deployment"

echo "📁 Project root: $PROJECT_ROOT"
echo "📁 Deployment dir: $DEPLOYMENT_DIR"

# Function to print status
print_status() {
    echo -e "${GREEN}✓${NC} $1"
}

print_warning() {
    echo -e "${YELLOW}⚠${NC} $1"
}

print_error() {
    echo -e "${RED}✗${NC} $1"
}

# Build web assets
build_web() {
    echo "🌐 Building web assets..."

    # Copy web files to deployment/web
    mkdir -p "$DEPLOYMENT_DIR/web"
    cp "$PROJECT_ROOT/index.html" "$DEPLOYMENT_DIR/web/"
    cp "$PROJECT_ROOT/app.js" "$DEPLOYMENT_DIR/web/"
    cp "$PROJECT_ROOT/admin.js" "$DEPLOYMENT_DIR/web/"
    cp "$PROJECT_ROOT/staff.js" "$DEPLOYMENT_DIR/web/"
    cp "$PROJECT_ROOT/data.js" "$DEPLOYMENT_DIR/web/"
    cp "$PROJECT_ROOT/styles.css" "$DEPLOYMENT_DIR/web/"

    print_status "Web assets copied to deployment/web"
}

# Build desktop assets
build_desktop() {
    echo "💻 Building desktop assets..."

    # Copy web files to desktop folder
    cp "$PROJECT_ROOT/index.html" "$PROJECT_ROOT/desktop/"
    cp "$PROJECT_ROOT/app.js" "$PROJECT_ROOT/desktop/"
    cp "$PROJECT_ROOT/admin.js" "$PROJECT_ROOT/desktop/"
    cp "$PROJECT_ROOT/staff.js" "$PROJECT_ROOT/desktop/"
    cp "$PROJECT_ROOT/data.js" "$PROJECT_ROOT/desktop/"
    cp "$PROJECT_ROOT/styles.css" "$PROJECT_ROOT/desktop/"

    print_status "Desktop assets copied"
}

# Build mobile assets
build_mobile() {
    echo "📱 Building mobile assets..."

    # Sync web assets to mobile www folder
    cd "$PROJECT_ROOT/mobile"
    if command -v npm &> /dev/null; then
        npm run sync 2>/dev/null || {
            print_warning "npm sync failed, copying manually"
            cp "$PROJECT_ROOT/index.html" "$PROJECT_ROOT/mobile/www/"
            cp "$PROJECT_ROOT/app.js" "$PROJECT_ROOT/mobile/www/"
            cp "$PROJECT_ROOT/admin.js" "$PROJECT_ROOT/mobile/www/"
            cp "$PROJECT_ROOT/staff.js" "$PROJECT_ROOT/mobile/www/"
            cp "$PROJECT_ROOT/data.js" "$PROJECT_ROOT/mobile/www/"
            cp "$PROJECT_ROOT/styles.css" "$PROJECT_ROOT/mobile/www/"
        }
    else
        print_warning "npm not found, copying manually"
        cp "$PROJECT_ROOT/index.html" "$PROJECT_ROOT/mobile/www/"
        cp "$PROJECT_ROOT/app.js" "$PROJECT_ROOT/mobile/www/"
        cp "$PROJECT_ROOT/admin.js" "$PROJECT_ROOT/mobile/www/"
        cp "$PROJECT_ROOT/staff.js" "$PROJECT_ROOT/mobile/www/"
        cp "$PROJECT_ROOT/data.js" "$PROJECT_ROOT/mobile/www/"
        cp "$PROJECT_ROOT/styles.css" "$PROJECT_ROOT/mobile/www/"
    fi

    print_status "Mobile assets synced"
}

# Build backend Docker images
build_backends() {
    echo "🐳 Building backend Docker images..."

    # Build Node.js backend
    cd "$PROJECT_ROOT/backend"
    docker build -t cafe-backend:latest .

    # Build Spring backend
    cd "$PROJECT_ROOT/backend-spring"
    docker build -t cafe-spring-backend:latest .

    print_status "Backend Docker images built"
}

# Main build process
main() {
    local targets=("$@")

    if [ ${#targets[@]} -eq 0 ]; then
        targets=("web" "desktop" "mobile" "backends")
    fi

    for target in "${targets[@]}"; do
        case $target in
            web)
                build_web
                ;;
            desktop)
                build_desktop
                ;;
            mobile)
                build_mobile
                ;;
            backends)
                build_backends
                ;;
            all)
                build_web
                build_desktop
                build_mobile
                build_backends
                ;;
            *)
                print_error "Unknown target: $target"
                echo "Available targets: web, desktop, mobile, backends, all"
                exit 1
                ;;
        esac
    done

    print_status "Build completed successfully!"
    echo ""
    echo "Next steps:"
    echo "  - For web: Deploy using deployment/docker/docker-compose.yml"
    echo "  - For desktop: Run 'npm run build' in desktop/ folder"
    echo "  - For mobile: Run 'npx cap build android' or 'npx cap build ios' in mobile/ folder"
    echo "  - For Kubernetes: Apply manifests in deployment/k8s/"
}

# Run main function with all arguments
main "$@"
