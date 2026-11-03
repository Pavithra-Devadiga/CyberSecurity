# Stage 1: Build Java Application
FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /app

COPY pom.xml .
RUN mvn dependency:go-offline -B

COPY src ./src
RUN mvn clean package -DskipTests

# Stage 2: Runtime Image
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# Add unprivileged user for security hardening
RUN addgroup -S appgroup && adduser -S appuser -G appgroup
USER appuser

COPY --from=build /app/target/cybersecurity-backend-1.0.0.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
