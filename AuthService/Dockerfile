FROM openjdk:17-jdk-slim

WORKDIR /app

# Copier le wrapper Maven
COPY mvnw .
COPY .mvn .mvn

# Copier le pom.xml et télécharger les dépendances
COPY pom.xml .
RUN chmod +x mvnw && ./mvnw dependency:go-offline -B

# Copier le code source
COPY src src

# Build de l'application
RUN ./mvnw clean package -DskipTests

# Exposer le port
EXPOSE 8084

# Commande pour démarrer l'application
ENTRYPOINT ["java", "-jar", "target/authService-0.0.1-SNAPSHOT.jar"] 