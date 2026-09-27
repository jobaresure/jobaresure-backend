# Build stage
FROM gradle:8.14-jdk17 AS build
WORKDIR /app
COPY build.gradle settings.gradle ./
COPY src ./src
RUN gradle clean bootJar --no-daemon

# Run stage
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY --from=build /app/build/libs/*.jar app1.jar
EXPOSE 18080
ENTRYPOINT ["java", "-jar", "app1.jar"]