FROM eclipse-temurin:17-jdk-alpine

WORKDIR /app

COPY target/task-manager-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-Xmx256m", "-Xms256m", "-jar", "app.jar"]