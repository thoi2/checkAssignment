FROM eclipse-temurin:17-jdk as builder
WORKDIR /app
COPY . .
RUN ./gradlew clean bootJar

FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=builder /app/build/libs/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","/app/app.jar"]
