FROM gradle:8.7-jdk21-jammy AS build

WORKDIR /workspace/app

COPY build.gradle settings.gradle gradlew ./
COPY gradle ./gradle

# Copy source code
COPY src ./src

# Build using the Gradle wrapper within the container
RUN ./gradlew build bootJar --no-daemon


FROM eclipse-temurin:21-jre-jammy

WORKDIR /app

# Copy *only* the built JAR from the 'build' stage
COPY --from=build /workspace/app/build/libs/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "/app/app.jar"]