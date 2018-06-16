FROM openjdk:8-jdk-alpine
VOLUME /tmp
ARG JAR_FILE
ADD ${JAR_FILE} /target/opera-datamapper.jar
COPY /target/application.properties /target/
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","/target/opera-datamapper.jar"]