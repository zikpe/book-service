# Dockerfile - will be ignored as docker compose is not using builds

# Use lightweight OpenJDK base image
FROM openjdk:17-jdk-slim

# Set a working directory inside the container
WORKDIR /app

# Copy the JAR file into the container (adjust jar name if needed)
COPY target/book-service-0.0.1-SNAPSHOT.jar app.jar

# Expose port (matches the app's port)
EXPOSE 8080

# Run the jar file
ENTRYPOINT ["java", "-jar", "app.jar"]