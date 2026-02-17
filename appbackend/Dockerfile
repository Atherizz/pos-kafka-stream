# Stage 1: Build stage
FROM maven:3.9.6-amazoncorretto-21-al2023 AS build
WORKDIR /app

# Copy pom.xml and download dependencies
COPY pom.xml .
RUN mvn dependency:go-offline

# Copy source code and build the application
COPY src ./src
RUN mvn clean package -DskipTests

# Stage 2: Run stage
FROM amazoncorretto:21-alpine3.18
WORKDIR /app

# Copy JAR from stage build
COPY --from=build /app/target/*.jar app.jar

ENV JAVA_OPTS="-Xmx512M -Xms256M"

EXPOSE 8080

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]