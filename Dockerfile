
FROM eclipse-temurin:21-jdk-jammy AS builder
WORKDIR /app
COPY .mvn/ .mvn
COPY mvnw pom.xml ./
COPY application ./application
COPY bootstrap ./bootstrap
COPY domain ./domain
COPY persistence-adapter ./persistence-adapter
COPY qrcode-adapter ./qrcode-adapter
COPY security-adapter ./security-adapter
COPY web-adapter ./web-adapter
RUN chmod +x ./mvnw && ./mvnw clean package -DskipTests -o # -o for offline


FROM eclipse-temurin:21-jre-jammy
WORKDIR /app
COPY --from=builder /app/bootstrap/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]