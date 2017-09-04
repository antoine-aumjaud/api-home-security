FROM openjdk:8-jre-alpine

LABEL maintainer "Antoine Aumjaud <antoine_dev@aumjaud.fr>"

EXPOSE 9080

WORKDIR /home/app
COPY build/libs/api-home-security-*.jar api-home-security.jar 
VOLUME ./conf
VOLUME ./logs

CMD    java -cp ./conf -jar api-home-security.jar
