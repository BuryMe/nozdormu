FROM maven:3.6.0-jdk-8-slim AS build
COPY ./ /app/
WORKDIR /app/
RUN mvn clean package -DskipTests

FROM openjdk:8-jre-alpine
COPY --from=build /app/target/*.jar nozdormu.jar
COPY ./src/main/resources/application.yml /config/application.yml
EXPOSE 8081
ENV db.host=xxx.xxx.xxx.xx
ENV db.port=xxx
ENV db.schema=xxxx
ENV db.username=xxxx
ENV db.password=xxxx
ENV mq.namesrv=xxxxxx
ENTRYPOINT ["sh", "-c", "java -Djava.security.egd=file:/dev/./urandom -jar /nozdormu.jar"]

CMD ["sh", "-c", "sed -i \"s/\${db.host}/$db.host/\" /config/application.yml && \
                 sed -i \"s/\${db.port}/$db.port/\" /config/application.yml && \
                 sed -i \"s/\${db.schema}/$db.schema/\" /config/application.yml && \
                 sed -i \"s/\${db.username}/$db.username/\" /config/application.yml && \
                 sed -i \"s/\${db.password}/$db.password/\" /config/application.yml && \
                 sed -i \"s/\${mq.namesrv}/$mq.namesrv/\" /config/application.yml && \
                 java -Djava.security.egd=file:/dev/./urandom -jar /nozdormu.jar"]


