FROM gradle:9.1-jdk25 as build
LABEL authors="anhel12"

ENV HOME=/home/app
RUN mkdir -p $HOME

COPY ./settings.gradle /usr/share/gradle/ref/

RUN mkdir -p $HOME/dispatcher
COPY ./dispatcher/build.gradle $HOME/dispatcher
COPY ./dispatcher/src $HOME/dispatcher/src
RUN gradle -p $HOME/dispatcher clean build -x test

#Run stage
FROM eclipse-temurin:25-jdk-alpine
ARG JAR_FILE=/home/app/dispatcher/build/libs/dispatcher-0.0.1.jar
COPY --from=build $JAR_FILE /opt/docker-file-sharing-bot/dispatcher.jar
ENTRYPOINT ["java", "-jar", "/opt/docker-file-sharing-bot/dispatcher.jar"]