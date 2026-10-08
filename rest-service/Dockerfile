FROM gradle:9.1-jdk25 AS build
LABEL authors="anhel12"

ENV HOME=/home/app
RUN mkdir -p $HOME

COPY ./settings.gradle /usr/share/gradle/ref/

RUN mkdir -p $HOME/common
COPY ./common $HOME/common
RUN gradle -p $HOME/common clean build -x test
RUN gradle -p $HOME/common publishToMavenLocal

RUN mkdir -p $HOME/rest-service
COPY ./rest-service/build.gradle $HOME/rest-service
COPY ./rest-service/src $HOME/rest-service/src
RUN gradle -p $HOME/rest-service clean build -x test

FROM eclipse-temurin:25-jdk-alpine
ARG JAR_FILE=/home/app/rest-service/build/libs/rest-service-0.0.1.jar
COPY --from=build $JAR_FILE /opt/docker-file-sharing-bot/rest-service.jar
ENTRYPOINT ["java", "-jar", "/opt/docker-file-sharing-bot/rest-service.jar"]