FROM openjdk:17-jdk-slim AS build
WORKDIR /app
COPY . /app
RUN apt-get update && apt-get install -y curl unzip
RUN ./gradlew build -x test


# Multi Stage Build
FROM openjdk:17-jdk-slim AS deployment

# KST Timezone Setup
RUN apt-get update && apt-get install -y tzdata
ENV TZ=Asia/Seoul

WORKDIR /app

COPY --from=build /app/build/libs/*.jar app.jar


ENTRYPOINT ["java","-jar","/app/app.jar"]
