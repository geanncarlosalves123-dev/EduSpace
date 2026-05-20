FROM eclipse-temurin:17

WORKDIR /app

COPY . .

RUN chmod +x mvnw || true

RUN ./mvnw clean package || mvn clean package

EXPOSE 8080

CMD ["java", "-jar", "target/eduspace-backend-1.0.0.jar"]