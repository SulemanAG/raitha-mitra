# Multi-stage Docker build for Raitha Mitra Spring Boot Backend
# Stage 1: Build stage
FROM eclipse-temurin:17-jdk-alpine AS build
WORKDIR /app

# Copy Maven wrapper and pom.xml first to maximize layer caching
COPY .mvn/ .mvn/
COPY mvnw pom.xml ./
RUN chmod +x mvnw

# Resolve dependencies offline if possible
RUN ./mvnw dependency:go-offline -B || true

# Copy source code and build production JAR
COPY src ./src
RUN ./mvnw clean package -DskipTests

# Stage 2: Lightweight runtime stage
FROM eclipse-temurin:17-jre-alpine AS runtime
WORKDIR /app

# Create non-root system user for security hardening
RUN addgroup -S appuser && adduser -S appuser -G appuser

# Copy generated Spring Boot executable JAR from build stage
COPY --from=build /app/target/*.jar app.jar

# Set ownership to appuser
RUN chown appuser:appuser app.jar
USER appuser

# Expose default port (Render will override via PORT environment variable)
EXPOSE 8080

# Configure JVM flags and launch executable JAR
ENTRYPOINT ["java", "-XX:+UseG1GC", "-XX:MaxRAMPercentage=75.0", "-jar", "app.jar"]
