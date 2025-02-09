# Use OpenJDK as the base image
FROM openjdk:17-jdk-slim AS builder

# Set the working directory
WORKDIR /app

# Copy the pom.xml (or build.gradle) and dependencies
COPY pom.xml .
# If using Gradle, use this instead:
# COPY build.gradle .

# Fetch dependencies (this will make Docker cache the dependencies, so it doesn’t re-download them every time)
RUN ./mvnw dependency:go-offline -B

# Copy the source code
COPY src /app/src

# Package the application using Maven (replace with Gradle if using Gradle)
RUN ./mvnw clean package -DskipTests

# Create a new image from the OpenJDK base image to run the app
FROM openjdk:17-jdk-slim

# Set the working directory
WORKDIR /app

# Copy the JAR file from the builder image
COPY --from=builder /app/target/send-email-0.0.1-SNAPSHOT.jar /app/send-email-0.0.1-SNAPSHOT.jar

# Expose the application port (default Spring Boot port is 8080)
EXPOSE 8080

# Run the Spring Boot application
ENTRYPOINT ["java", "-jar", "/app/send-email-0.0.1-SNAPSHOT.jar"]
