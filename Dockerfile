
FROM eclipse-temurin:21-jdk-jammy AS builder
WORKDIR /app
COPY .mvn/ .mvn
COPY mvnw pom.xml ./
COPY src ./src
RUN chmod +x ./mvnw && ./mvnw clean package -DskipTests


FROM eclipse-temurin:21-jre-jammy
WORKDIR /app
COPY --from=builder /app/target/book-it-backend-*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]