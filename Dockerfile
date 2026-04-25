# Stage 1: Build inside Docker
FROM maven:3.9.9-eclipse-temurin-21 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

# Stage 2: Run the application
FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar

# Your personalized Identity (Grader checks these)
ENV USER_NAME=Docker_Mark_Maged
ENV ID=Docker_55_1588

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]