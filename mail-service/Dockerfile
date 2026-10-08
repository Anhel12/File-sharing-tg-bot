FROM gradle:9.1-jdk25 as build
LABEL authors="anhel12"

ENV HOME=/home/app
RUN mkdir -p $HOME

COPY ./settings.gradle /usr/share/gradle/ref/

RUN mkdir -p $HOME/common
COPY ./common $HOME/common
RUN gradle -p $HOME/common clean build -x test
RUN gradle -p $HOME/common publishToMavenLocal

RUN mkdir -p $HOME/mail-service
COPY ./mail-service/build.gradle $HOME/mail-service
COPY ./mail-service/src $HOME/mail-service/src
RUN gradle -p $HOME/mail-service clean build -x test

#Run stage
FROM eclipse-temurin:25-jdk-alpine
ARG JAR_FILE=/home/app/mail-service/build/libs/mail-service-0.0.1.jar
COPY --from=build $JAR_FILE /opt/docker-file-sharing-bot/mail-service.jar
ENTRYPOINT ["java", "-jar", "/opt/docker-file-sharing-bot/mail-service.jar"]