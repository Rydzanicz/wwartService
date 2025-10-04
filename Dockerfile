FROM eclipse-temurin:21-jre-alpine

# Install certificates and timezone
RUN apk add --no-cache ca-certificates tzdata && \
    ln -sf /usr/share/zoneinfo/Europe/Warsaw /etc/localtime

# Create non-root user
RUN addgroup -g 1000 spring && \
    adduser -D -s /bin/sh -u 1000 -G spring spring

WORKDIR /app

# Copy application jar
COPY --chown=spring:spring build/libs/wwartService-0.0.1-SNAPSHOT.jar app.jar

# Copy certificates if needed
COPY --chown=spring:spring certs/ /app/certs/

# Create logs directory
RUN mkdir -p /var/log/wwartService && \
    chown -R spring:spring /var/log/wwartService

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
