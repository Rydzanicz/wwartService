# Stage 1: Build aplikacji
FROM gradle:8-jdk21-alpine AS build
WORKDIR /app

# Kopiuj pliki gradle
COPY build.gradle settings.gradle ./
COPY gradle gradle/

# Kopiuj kod źródłowy
COPY src src/

# Zbuduj aplikację
RUN gradle clean build -x test --no-daemon

# Stage 2: Runtime
FROM eclipse-temurin:21-jre-alpine

# Install certificates and timezone
RUN apk add --no-cache ca-certificates tzdata && \
    ln -sf /usr/share/zoneinfo/Europe/Warsaw /etc/localtime

# Create non-root user
RUN addgroup -g 1000 spring && \
    adduser -D -s /bin/sh -u 1000 -G spring spring

WORKDIR /app

# Kopiuj JAR z build stage
COPY --from=build --chown=spring:spring /app/build/libs/wwartService-0.0.1-SNAPSHOT.jar app.jar

# Create logs directory
RUN mkdir -p /var/log/wwartService /tmp/uploads && \
    chown -R spring:spring /var/log/wwartService /tmp/uploads

# Switch to non-root user
USER spring

# Health check endpoint
HEALTHCHECK --interval=30s --timeout=3s --start-period=5s --retries=3 \
    CMD wget --no-verbose --tries=1 --spider http://localhost:8080/actuator/health || exit 1

EXPOSE 8080

# JVM optimizations for production
ENTRYPOINT ["java", \
    "-XX:+UseContainerSupport", \
    "-XX:MaxRAMPercentage=70.0", \
    "-XX:+UseG1GC", \
    "-XX:+UseStringDeduplication", \
    "-Dspring.profiles.active=prod", \
    "-jar", "/app/app.jar"]
