FROM eclipse-temurin:21-jdk-jammy
WORKDIR /app

# Copiar el jar generado (asegúrate de ejecutar 'mvnw package' antes)
COPY target/*.jar app.jar

# Puerto por defecto (Spring Boot)
EXPOSE 8080

ENTRYPOINT ["java","-jar","/app/app.jar"]
