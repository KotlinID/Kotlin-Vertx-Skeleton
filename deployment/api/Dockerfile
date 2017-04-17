FROM openjdk
MAINTAINER Deny Prasetyo <jasoet87@gmail.com>

RUN mkdir -p /var/log/kotlin && chmod -R 777 /var/log/kotlin

COPY kotlin-api.jar /

VOLUME ["/var/log","/var/image/"]

CMD ["java","-jar","kotlin-api.jar"]