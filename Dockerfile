# Use modern Java 17 base image
FROM eclipse-temurin:22-jdk

# Create working directory
WORKDIR /app

# Copy jar file
COPY target/*.jar app.jar

EXPOSE 8083

# Run application
ENTRYPOINT ["java", "-jar", "app.jar"]