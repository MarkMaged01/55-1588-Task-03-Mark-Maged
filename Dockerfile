# Stage 1: Build
FROM maven:3.9.9-eclipse-temurin-25-alpine AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

# Stage 2: Run
FROM eclipse-temurin:25.0.2_10-jdk-alpine
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar

ENV USER_NAME=Docker_Mark_Maged
ENV ID=Docker_55_1588

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]