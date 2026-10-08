FROM gradle:9.1-jdk25 as build
LABEL authors="anhel12"

ENV HOME=/home/app
RUN mkdir -p $HOME

COPY ./settings.gradle /usr/share/gradle/ref/

RUN mkdir -p $HOME/common
COPY ./common $HOME/common
RUN gradle -p $HOME/common clean build -x test
RUN gradle -p $HOME/common publishToMavenLocal

RUN mkdir -p $HOME/node
COPY ./node/build.gradle $HOME/node
COPY ./node/src $HOME/node/src
RUN gradle -p $HOME/node clean build -x test

#Run stage
FROM eclipse-temurin:25-jdk-alpine
ARG JAR_FILE=/home/app/node/build/libs/node-0.0.1.jar
COPY --from=build $JAR_FILE /opt/docker-file-sharing-bot/node.jar
ENTRYPOINT ["java", "-jar", "/opt/docker-file-sharing-bot/node.jar"]