# Microservices Food Ordering System

This repository contains a microservices-based food ordering system, including backend services and a frontend application.

## Project Structure

- **AuthService/**: Handles authentication and user management (Java Spring Boot)
- **DeliveryService/**: Manages delivery operations (Java Spring Boot)
- **kitchenService/**: Manages kitchen operations (Java Spring Boot)
- **orderService/**: Handles order management (Java Spring Boot)
- **food-ordering-app/**: Frontend application (Angular)
- **postgres-init/**: Database initialization scripts
- **compose.yml**: Docker Compose configuration for running all services

## Prerequisites
- Java 17+
- Node.js & npm
- Angular CLI
- Docker & Docker Compose

## Getting Started

### Backend Services
1. Navigate to each service directory (e.g., `AuthService/`).
2. Build the service:
   ```sh
   ./mvnw clean package
   ```
3. Run the service:
   ```sh
   java -jar target/*.jar
   ```

### Frontend (Angular)
1. Navigate to `food-ordering-app/`.
2. Install dependencies:
   ```sh
   npm install
   ```
3. Start the development server:
   ```sh
   ng serve
   ```

### Docker Compose
To run all services together:
```sh
docker compose up --build
```

## Environment Variables
Refer to each service's documentation and `.env` files for configuration.

## License
This project is for educational purposes.
