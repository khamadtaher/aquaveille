# Étape 1 : compilation avec Maven
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app
COPY pom.xml .
RUN mvn -q dependency:go-offline
COPY src ./src
RUN mvn -q package -DskipTests

# Étape 2 : image d'exécution légère
FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=build /app/target/aquaveille-backend-*.jar app.jar
EXPOSE 8080
# Limite la mémoire de la JVM (hébergement gratuit : 512 Mo)
ENTRYPOINT ["java", "-XX:MaxRAMPercentage=75", "-jar", "app.jar"]
