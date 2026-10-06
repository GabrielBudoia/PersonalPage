# ---- Build stage: compiles the jar ----
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app

# Dependencies first, so Docker caches them when only the code changes
COPY pom.xml .
RUN mvn -q -B dependency:go-offline

COPY src ./src
# Tests need Docker (Testcontainers); they run locally before each push
RUN mvn -q -B package -DskipTests

# ---- Run stage: only the JRE and the jar ----
FROM eclipse-temurin:17-jre
WORKDIR /app

# Never run the application as root
RUN useradd --system --uid 1001 spring
COPY --from=build /app/target/*.jar app.jar
USER spring

EXPOSE 8080
ENTRYPOINT ["java", "-XX:MaxRAMPercentage=60", "-jar", "app.jar"]
