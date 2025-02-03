FROM openjdk:17-jdk-slim AS build
WORKDIR /app
COPY . /app
RUN apt-get update && apt-get install -y curl unzip
RUN ./gradlew build


# Multi Stage Build
FROM openjdk:17-jdk-slim AS deployment
WORKDIR /app

COPY --from=build /app/build/libs/*.jar app.jar

ENTRYPOINT ["java","-jar","/app/app.jar"]
