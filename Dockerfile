# ========================================
# Stage 1: Builder
# ========================================
FROM eclipse-temurin:21-jdk-alpine AS builder

LABEL maintainer="SleekFlow <dev@sleekflow.com>"

# Install Maven and bash
RUN apk add --no-cache bash maven

WORKDIR /app

# Copy pom.xml first (for better Docker layer caching)
COPY pom.xml .

# Download dependencies (cached if pom.xml unchanged)
RUN mvn dependency:go-offline -B

# Copy source code
COPY src ./src

# Build application
RUN mvn clean package -DskipTests -B && \
    mv target/*.jar target/app.jar

# ========================================
# Stage 2: Runtime
# ========================================
FROM eclipse-temurin:21-jre-alpine

LABEL maintainer="SleekFlow <dev@sleekflow.com>"

# Install curl for healthcheck
RUN apk add --no-cache curl

# Create non-root user
RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring

WORKDIR /app

# Copy JAR from builder stage
COPY --from=builder /app/target/app.jar app.jar

# Expose port
EXPOSE 8080

# Health check
HEALTHCHECK --interval=30s --timeout=3s --start-period=60s --retries=3 \
    CMD curl -f http://localhost:8080/actuator/health || exit 1

# Run application
ENTRYPOINT ["java", \
    "-Djava.security.egd=file:/dev/./urandom", \
    "-Dspring.profiles.active=docker", \
    "-jar", \
    "app.jar"]
