# Use Amazon Corretto JDK as base image
FROM amazoncorretto:17

# Set working directory inside the container
WORKDIR /app

# Copy your built JAR file into the container
COPY target/todoapp-0.0.1-SNAPSHOT.jar app.jar

# Run the JAR
ENTRYPOINT ["java", "-jar", "app.jar"]
