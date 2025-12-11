# ================================
#  Stage 1: Build with Maven
# ================================
FROM maven:3.8.5-openjdk-8 AS build

WORKDIR /app

COPY pom.xml .
COPY src ./src

RUN mvn -e -B -DskipTests=true clean package

# ================================
#  Stage 2: Runtime
# ================================
FROM openjdk:8-jre-slim

WORKDIR /app

COPY --from=build /app/target/binance-u-analyzer-1.0.jar app.jar

EXPOSE 4567

ENTRYPOINT ["java", "-jar", "app.jar"]
