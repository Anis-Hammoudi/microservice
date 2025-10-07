# Stage 1: Build the application using Maven
FROM openjdk:17-jdk-slim AS builder

WORKDIR /app

# Copy Maven wrapper and pom.xml for dependency caching
COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .

# Grant execution permissions and download dependencies
RUN chmod +x mvnw && ./mvnw dependency:go-offline -B

# Copy the rest of the source code and build the application
COPY src src
RUN ./mvnw clean package -DskipTests


# --------------------------------------------------------------------
# Stage 2: Create the final, smaller runtime image
FROM openjdk:17-jdk-slim

# MOVED THE INSTALL COMMAND HERE
RUN apt-get update && apt-get install -y curl && rm -rf /var/lib/apt/lists/*

WORKDIR /app

# Create a non-root user for better security
RUN addgroup --system spring && adduser --system --ingroup spring spring
USER spring:spring

# Copy only the built JAR file from the 'builder' stage
COPY --from=builder /app/target/*.jar app.jar

# Expose the port the application runs on
EXPOSE 8083

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]
