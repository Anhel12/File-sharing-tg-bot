FROM gradle:9.1-jdk25
LABEL authors="anhel12"

ENV HOME=/home/app
RUN mkdir -p $HOME

ADD ./settings.gradle /usr/share/gradle/ref/
ENV GRADLE_SETTINGS=/usr/share/gradle/ref/settings.gradle

RUN mkdir -p $HOME/dispatcher
ADD ./dispatcher/build.gradle $HOME/dispatcher
ADD ./dispatcher/src $HOME/dispatcher/src
RUN gradle -p $HOME/dispatcher clean build --debug

CMD java -jar $HOME/dispatcher/build/libs/dispatcher-0.0.1.jar