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

# Install Chrome Browser
#RUN apt-get update && apt-get install -y \
#   chromium \
#   && rm -rf /var/lib/apt/lists/*

# 최신 Chromium
RUN apt-get update && apt-get install -y wget unzip curl && rm -rf /var/lib/apt/lists/*

RUN CHROMIUM_LATEST=$(curl -s https://versionhistory.googleapis.com/v1/chrome/platforms/linux/channels/stable/versions | jq -r '.versions[0].version') \
    && wget -q "https://commondatastorage.googleapis.com/chromium-browser-snapshots/Linux_x64/${CHROMIUM_LATEST}/chrome-linux.zip" -O /tmp/chromium.zip \
    && unzip /tmp/chromium.zip -d /opt/chromium \
    && ln -s /opt/chromium/chrome /usr/local/bin/chromium \
    && rm /tmp/chromium.zip


ENTRYPOINT ["java","-jar","/app/app.jar"]
