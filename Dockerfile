FROM openjdk:17-jdk-slim AS build
WORKDIR /app
COPY . /app
RUN apt-get update && apt-get install -y curl unzip wget
RUN ./gradlew build -x test


# Multi Stage Build
FROM openjdk:17-jdk-slim AS deployment

# KST Timezone Setup
RUN apt-get update && apt-get install -y tzdata
ENV TZ=Asia/Seoul

WORKDIR /app

COPY --from=build /app/build/libs/*.jar app.jar

# Install Chrome Browser
#RUN apt-get update && apt-get install -y \
#   chromium \
#   && rm -rf /var/lib/apt/lists/*

# Install Google Chrome
#RUN curl -LO https://dl.google.com/linux/direct/google-chrome-stable_current_amd64.deb
#RUN apt-get install -y ./google-chrome-stable_current_amd64.deb
#RUN apt-get update && apt-get install -y -f
#RUN rm google-chrome-stable_current_amd64.deb
#RUN apt-get clean && rm -rf /var/lib/apt/lists/*

RUN wget -q https://dl.google.com/linux/direct/google-chrome-stable_current_amd64.deb
RUN apt-get install -y ./google-chrome-stable_current_amd64.deb
RUN rm ./google-chrome-stable_current_amd64.deb


ENTRYPOINT ["java","-jar","/app/app.jar"]
