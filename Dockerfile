FROM eclipse-temurin:17-jdk-alpine

ARG PROFILE
ARG ADDITIONAL_OPTS

ENV PROFILE=${PROFILE}
ENV ADDITIONAL_OPTS=${ADDTIONAL_OPTS}

WORKDIR /opt/app

COPY /target/crudetota*.jar crudetotav1.jar

SHELL ["/bin/sh", "-c"]

EXPOSE 5005
EXPOSE 8080

CMD java ${ADDTIONAL_OPTS} -jar crudetotav1.jar --spring.profiles.active=${PROFILE}