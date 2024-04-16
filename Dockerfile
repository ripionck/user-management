# Use an appropriate base image for your Java application
FROM openjdk:17

# Set the working directory in the container
WORKDIR /app

# Copy the application JAR file into the container
COPY target/userManagement-0.0.1-SNAPSHOT.jar /app/userManagement-0.0.1-SNAPSHOT.jar

# Command to run the application when the container starts
CMD ["java", "-jar", "userManagement-0.0.1-SNAPSHOT.jar"]
