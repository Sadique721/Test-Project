#FROM openjdk:8
FROM eclipse-temurin:8-jdk
ARG JAR_FILE=build/libs/SavbillRadius-0.0.1-SNAPSHOT.jar
COPY ${JAR_FILE} savbillRadius.jar
EXPOSE 9090  
EXPOSE 1812/udp
EXPOSE 1812/tcp
EXPOSE 1813/udp
EXPOSE 1813/tcp
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar", "/savbillRadius.jar"]