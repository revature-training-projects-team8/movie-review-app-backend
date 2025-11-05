# Option A – Eclipse Temurin (well supported)
FROM eclipse-temurin:17-jdk-jammy

# Set working directory
WORKDIR /app

# Copy jar file built by Maven
COPY target/*.jar app.jar

# Expose the port your app runs on
EXPOSE 8088

# Run the app
ENTRYPOINT ["java", "-jar", "app.jar"]
