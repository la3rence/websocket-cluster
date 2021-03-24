FROM openjdk:8-jdk-alpine
VOLUME /tmp
ARG JAR_FILE=target/gateway-1.0.0.jar
COPY ${JAR_FILE} app.jar
ENTRYPOINT java ${JAVA_OPTS} -Duser.timezone=GMT+08 -Djava.security.egd=file:/dev/./urandom -jar /app.jar