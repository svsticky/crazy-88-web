FROM eclipse-temurin:24 as build
WORKDIR /app
COPY . /app
RUN ["./gradlew", "clean", "bootJar"]

FROM eclipse-temurin:24-jre
WORKDIR /app
COPY --from=build /app/build/libs/*.jar /app/app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
