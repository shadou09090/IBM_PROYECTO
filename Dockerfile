# Stage 1: Build the Spring Boot application
FROM maven:3.9-eclipse-temurin-17 AS builder
WORKDIR /build

COPY app/pom.xml ./pom.xml
COPY app/checkstyle.xml ./checkstyle.xml
COPY app/src ./src

RUN mvn -B clean package -DskipTests

# Stage 2: Minimal non-root JRE runtime
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

RUN addgroup -S appgroup && adduser -S appuser -G appgroup

COPY --from=builder /build/target/*.jar app.jar
RUN chown appuser:appgroup app.jar

USER appuser
EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
