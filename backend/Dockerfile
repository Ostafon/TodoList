FROM ubuntu:latest
LABEL authors="sasha"

FROM openjdk:21-jdk
COPY target/todo-service.jar todo-service.jar
ENTRYPOINT ["java", "-jar", "todo-service.jar"]