FROM gradle:9.1-jdk25
LABEL authors="anhel12"

ENV HOME=/home/app
RUN mkdir -p $HOME

ADD ./settings.gradle /usr/share/gradle/ref/
ENV GRADLE_SETTINGS=/usr/share/gradle/ref/settings.gradle

RUN mkdir -p $HOME/common
ADD ./common $HOME/common
RUN gradle -p $HOME/common clean build
RUN gradle -p $HOME/common publishToMavenLocal --debug

RUN mkdir -p $HOME/rest-service
ADD ./rest-service/build.gradle $HOME/rest-service
ADD ./rest-service/src $HOME/rest-service/src
RUN gradle -p $HOME/rest-service clean build --debug

CMD java -jar $HOME/rest-service/build/libs/rest-service-0.0.1.jar