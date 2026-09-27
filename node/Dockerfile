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

RUN mkdir -p $HOME/node
ADD ./node/build.gradle $HOME/node
ADD ./node/src $HOME/node/src
RUN gradle -p $HOME/node clean build --debug

CMD java -jar $HOME/node/build/libs/node-0.0.1.jar