FROM maven:3.6.0-jdk-8-slim AS build
COPY ./ /app/
WORKDIR /app/
RUN mvn clean package -DskipTests

FROM openjdk:8-jre-alpine
VOLUME /tmp
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} nozdormu.jar
COPY ./src/main/resources/application.yml /config/application.yml
EXPOSE 8081
ENV db.host=xxx
ENV db.port=xxx
ENV db.username=xxx
ENV db.password=xxx
ENV mq.namesrv=xxxx
ENTRYPOINT ["sh", "-c", "java -Djava.security.egd=file:/dev/./urandom -jar /nozdormu.jar"]

CMD ["sh", "-c", "sed -i \"s/\${db.host}/$db.host/\" /config/application.yml && \
                 sed -i \"s/\${db.port}/$db.port/\" /config/application.yml && \
                 sed -i \"s/\${db.username}/$db.username/\" /config/application.yml && \
                 sed -i \"s/\${db.password}/$db.password/\" /config/application.yml && \
                 sed -i \"s/\${mq.namesrv}/$mq.namesrv/\" /config/application.yml && \
                 java -Djava.security.egd=file:/dev/./urandom -jar /nozdormu.jar"]


