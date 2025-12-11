# ================================
# Stage 1: Build with Maven (JDK 8)
# ================================
FROM maven:3.8.5-eclipse-temurin-8 AS build

WORKDIR /app

COPY pom.xml .
COPY src ./src

RUN mvn -e -B -DskipTests=true clean package

# ================================
# Stage 2: Runtime (JRE 8)
# ================================
FROM eclipse-temurin:8-jre

WORKDIR /app

COPY --from=build /app/target/binance-u-analyzer-1.0.jar app.jar

EXPOSE 4567

ENTRYPOINT ["java", "-jar", "app.jar"]
