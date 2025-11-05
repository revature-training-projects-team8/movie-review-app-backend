# Use OpenJDK 17 as base image
FROM openjdk:17-slim

# Set working directory
WORKDIR /app

# Copy the built JAR from target directory
COPY movie-review-app-backend/target/*.jar app.jar

# Expose port 8088 (instead of default 8080)
EXPOSE 8088

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]