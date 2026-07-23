#FROM openjdk:8
#FROM eclipse-temurin:17-jdk
FROM dirainfotech/openjdk:8
ARG JAR_FILE=build/libs/TaskManagement-0.0.1-SNAPSHOT.jar
COPY ${JAR_FILE} TaskManagement.jar
EXPOSE 30081
ENTRYPOINT ["java", "-jar", "/TaskManagement.jar"]
