#!/bin/bash

# Kubernetes Deployment Script for Cafe Webapp
# This script deploys the application to a Kubernetes cluster

set -e

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m'

# Configuration
NAMESPACE=${K8S_NAMESPACE:-cafe-production}
PROJECT_ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
K8S_DIR="$PROJECT_ROOT/k8s"

# Functions
print_status() {
    echo -e "${GREEN}✓${NC} $1"
}

print_warning() {
    echo -e "${YELLOW}⚠${NC} $1"
}

print_error() {
    echo -e "${RED}✗${NC} $1"
}

print_info() {
    echo -e "${BLUE}ℹ${NC} $1"
}

# Check prerequisites
check_prerequisites() {
    print_info "Checking prerequisites..."

    if ! command -v kubectl &> /dev/null; then
        print_error "kubectl is not installed. Please install it first."
        exit 1
    fi

    if ! command -v docker &> /dev/null; then
        print_error "Docker is not installed. Please install it first."
        exit 1
    fi

    if ! kubectl cluster-info &> /dev/null; then
        print_error "Unable to connect to Kubernetes cluster. Please configure kubectl."
        exit 1
    fi

    print_status "Prerequisites check passed"
}

# Create namespace
create_namespace() {
    print_info "Creating namespace: $NAMESPACE"

    kubectl create namespace "$NAMESPACE" --dry-run=client -o yaml | kubectl apply -f -

    print_status "Namespace created"
}

# Build and push Docker images
build_and_push_images() {
    print_info "Building and pushing Docker images..."

    # Login to container registry (configure as needed)
    # echo "$DOCKER_PASSWORD" | docker login -u "$DOCKER_USERNAME" --password-stdin

    # Build Node.js backend
    cd "$PROJECT_ROOT/../backend"
    docker build -t cafe-backend:${GITHUB_SHA:-latest} .
    # docker push cafe-backend:${GITHUB_SHA:-latest}

    # Build Spring backend
    cd "$PROJECT_ROOT/../backend-spring"
    docker build -t cafe-spring-backend:${GITHUB_SHA:-latest} .
    # docker push cafe-spring-backend:${GITHUB_SHA:-latest}

    print_status "Docker images built"
}

# Apply secrets
apply_secrets() {
    print_info "Applying secrets..."

    if [ ! -f ".env" ]; then
        print_error ".env file not found. Please create it from .env.example"
        exit 1
    fi

    # Create secrets from .env file
    kubectl create secret generic cafe-secrets \
        --from-env-file=.env \
        --namespace="$NAMESPACE" \
        --dry-run=client -o yaml | kubectl apply -f -

    print_status "Secrets applied"
}

# Deploy PostgreSQL
deploy_postgres() {
    print_info "Deploying PostgreSQL..."

    kubectl apply -f "$K8S_DIR/postgres-statefulset.yaml" -n "$NAMESPACE"

    # Wait for PostgreSQL to be ready
    print_info "Waiting for PostgreSQL to be ready..."
    kubectl wait --for=condition=ready pod -l app=postgres -n "$NAMESPACE" --timeout=300s

    print_status "PostgreSQL deployed"
}

# Deploy backends
deploy_backends() {
    print_info "Deploying backend services..."

    # Deploy Node.js backend
    kubectl apply -f "$K8S_DIR/backend-node-deployment.yaml" -n "$NAMESPACE"

    # Deploy Spring backend
    kubectl apply -f "$K8S_DIR/backend-spring-deployment.yaml" -n "$NAMESPACE"

    # Apply HPA
    kubectl apply -f "$K8S_DIR/hpa.yaml" -n "$NAMESPACE"

    print_status "Backend services deployed"
}

# Deploy ingress
deploy_ingress() {
    print_info "Deploying ingress..."

    # Note: Ingress controller must be installed separately
    # For example: kubectl apply -f https://raw.githubusercontent.com/kubernetes/ingress-nginx/controller-v1.8.1/deploy/static/provider/cloud/deploy.yaml

    kubectl apply -f "$K8S_DIR/backend-node-deployment.yaml" -n "$NAMESPACE"
    kubectl apply -f "$K8S_DIR/backend-spring-deployment.yaml" -n "$NAMESPACE"

    print_status "Ingress deployed"
}

# Check deployment status
check_deployment() {
    print_info "Checking deployment status..."

    # Check pods
    kubectl get pods -n "$NAMESPACE"

    # Check services
    kubectl get services -n "$NAMESPACE"

    # Check ingress
    kubectl get ingress -n "$NAMESPACE"

    print_status "Deployment check completed"
}

# Main deployment function
deploy() {
    echo "🚀 Starting Cafe Webapp Kubernetes Deployment"
    echo "Namespace: $NAMESPACE"
    echo ""

    check_prerequisites
    create_namespace
    build_and_push_images
    apply_secrets
    deploy_postgres
    deploy_backends
    deploy_ingress
    check_deployment

    print_status "Deployment completed successfully!"
    echo ""
    print_info "Useful commands:"
    echo "  kubectl get pods -n $NAMESPACE"
    echo "  kubectl logs -f deployment/cafe-backend-node -n $NAMESPACE"
    echo "  kubectl logs -f deployment/cafe-backend-spring -n $NAMESPACE"
}

# Rollback function
rollback() {
    print_warning "Rolling back deployment..."

    kubectl rollout undo deployment/cafe-backend-node -n "$NAMESPACE"
    kubectl rollout undo deployment/cafe-backend-spring -n "$NAMESPACE"

    print_status "Rollback completed"
}

# Help function
help() {
    echo "Cafe Webapp Kubernetes Deployment Script"
    echo ""
    echo "Usage: $0 [command]"
    echo ""
    echo "Commands:"
    echo "  deploy    Deploy the application to Kubernetes"
    echo "  rollback  Rollback the last deployment"
    echo "  status    Check deployment status"
    echo "  help      Show this help message"
    echo ""
    echo "Environment variables:"
    echo "  K8S_NAMESPACE  Kubernetes namespace (default: cafe-production)"
}

# Main script logic
case "${1:-deploy}" in
    deploy)
        deploy
        ;;
    rollback)
        rollback
        ;;
    status)
        check_deployment
        ;;
    help|--help|-h)
        help
        ;;
    *)
        print_error "Unknown command: $1"
        help
        exit 1
        ;;
esac
