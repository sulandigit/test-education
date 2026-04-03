FROM eclipse-temurin:17-jdk
MAINTAINER roncoo
ARG JAVA_OPTS="-Xmx128M"
ENV JAVA_OPTS=$JAVA_OPTS
ARG RUN_ARGS="--spring.profiles.active=prod"
ENV RUN_ARGS=$RUN_ARGS
ADD target/course.jar /course.jar
ENTRYPOINT ["sh","-c","java $JAVA_OPTS -jar /course.jar $RUN_ARGS"]
