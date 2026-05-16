# Stage 1: Build & Package
FROM eclipse-temurin:17-jdk-alpine AS builder
WORKDIR /workspace

COPY pom.xml .
COPY mvnw* .
COPY .mvn .mvn
COPY src src

RUN chmod +x ./mvnw || true
RUN ./mvnw clean package -DskipTests

# Stage 2: Minimal Hardened Production Runtime
FROM eclipse-temurin:17-jre-alpine AS runner
WORKDIR /app

# Add non-root security group and user for PCI-DSS/SOC2 compliance
RUN addgroup -S appgroup -g 1001 && adduser -S appuser -u 1001 -G appgroup

# Copy compiled JAR artifact from builder
COPY --from=builder /workspace/target/*.jar app.jar
RUN chown -R appuser:appgroup /app

USER appuser:appgroup

EXPOSE 8080

HEALTHCHECK --interval=10s --timeout=5s --start-period=30s --retries=3 \
  CMD wget --no-verbose --tries=1 --spider http://localhost:8080/actuator/health || exit 1

ENTRYPOINT ["java", "-XX:+UseG1GC", "-XX:MaxRAMPercentage=75.0", "-jar", "app.jar"]
