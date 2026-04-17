# Cafe-Webapp Microservices Architecture

This directory contains the microservices implementation of the cafe-webapp, following industry standards for scalability and maintainability.

## Services Overview

- **api-gateway**: Entry point for all API requests, routes to appropriate services
- **user-service**: Handles authentication, OTP, customer sessions, and user management
- **menu-service**: Manages menu items and categories
- **order-service**: Handles order creation, status updates, and payment
- **notification-service**: Sends SMS and email notifications
- **admin-service**: Admin configurations and super-admin features

## Architecture Principles

- **Domain-Driven Design**: Each service owns its domain logic
- **API Gateway**: Centralized routing and cross-cutting concerns
- **Shared Database (Initial)**: Single PostgreSQL database for simplicity
- **RESTful Communication**: Synchronous calls between services
- **Event-Driven (Future)**: Async communication via RabbitMQ/Kafka

## Getting Started

1. **Prerequisites**:
   - Java 17
   - Maven
   - PostgreSQL

2. **Database Setup**:
   - Create database: `cafe_db`
   - Run migrations (Flyway handles this)

3. **Run Services**:
   ```bash
   # Start in order
   cd api-gateway && mvn spring-boot:run
   cd user-service && mvn spring-boot:run
   cd menu-service && mvn spring-boot:run
   # etc.
   ```

4. **Ports**:
   - API Gateway: 8080
   - User Service: 8081
   - Menu Service: 8082
   - Order Service: 8083
   - Notification Service: 8084
   - Admin Service: 8085

## Deployment

- Use Railway for each service as separate apps
- Configure environment variables for database and inter-service URLs
- Use Docker for containerization

## Next Steps

1. Complete remaining services (menu, order, notification, admin)
2. Implement inter-service communication
3. Add service discovery (Eureka)
4. Separate databases per service
5. Add monitoring and logging
6. Implement CI/CD pipelines

## Migration from Monolithic

- Extract logic from `backend/src/server.ts` to respective services
- Update frontend to point to API Gateway (port 8080)
- Migrate data and test end-to-end
