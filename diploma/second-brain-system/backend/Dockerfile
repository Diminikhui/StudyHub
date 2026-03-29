FROM gradle:8.13.0-jdk21-alpine AS build
WORKDIR /workspace

COPY gradlew gradlew
COPY gradle gradle
COPY build.gradle settings.gradle ./
COPY src src

RUN chmod +x gradlew && ./gradlew --no-daemon clean bootJar

FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

RUN addgroup -S app && adduser -S app -G app

COPY --from=build /workspace/build/libs/*.jar app.jar

EXPOSE 8080

USER app

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar /app/app.jar"]
