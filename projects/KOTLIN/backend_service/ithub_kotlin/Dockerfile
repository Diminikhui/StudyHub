FROM gradle:9.3.0-jdk21 AS builder

WORKDIR /workspace
COPY . .
RUN ./gradlew :api:buildFatJar --no-daemon

FROM eclipse-temurin:21-jre

WORKDIR /app
COPY --from=builder /workspace/api/build/libs/api-all.jar /app/app.jar
COPY --from=builder /workspace/data/src/main/resources/db/migration /app/db/migration

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "/app/app.jar"]
