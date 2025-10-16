
FROM maven:3.9.9-eclipse-temurin-21-jammy AS builder
WORKDIR /app
COPY .mvn/ .mvn
COPY mvnw pom.xml settings.xml ./
COPY application ./application
COPY bootstrap ./bootstrap
COPY domain ./domain
COPY persistence-adapter ./persistence-adapter
COPY qrcode-adapter ./qrcode-adapter
COPY security-adapter ./security-adapter
COPY email-notification-adapter ./email-notification-adapter
COPY notification-queue-adapter ./notification-queue-adapter
COPY ai-agent-adapter ./ai-agent-adapter
COPY s3-adapter ./s3-adapter
COPY web-adapter ./web-adapter
COPY telegram-bot-notification-adapter ./telegram-bot-notification-adapter
RUN  mvn clean package


FROM eclipse-temurin:21-jre-jammy
WORKDIR /app
COPY --from=builder /app/bootstrap/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]