FROM maven:3.8.6-eclipse-temurin-17 AS builder

WORKDIR /app

# Copia o pom.xml e baixa dependências (cache)
COPY pom.xml .
RUN mvn dependency:go-offline

# Copia o código-fonte e compila
COPY src ./src
RUN mvn clean package -DskipTests

FROM eclipse-temurin:17-jdk-alpine

WORKDIR /app

# Copia o JAR gerado do estágio anterior
COPY --from=builder /app/target/task-manager-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-Xmx256m", "-Xms256m", "-jar", "app.jar"]