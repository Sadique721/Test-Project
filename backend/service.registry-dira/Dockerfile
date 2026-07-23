FROM openjdk:8
ARG JAR_FILE=build/libs/SavbillServiceRegistry-0.0.1-SNAPSHOT.jar
COPY ${JAR_FILE} serviceRegistry.jar
EXPOSE 8761
ENTRYPOINT ["java", "-jar", "/serviceRegistry.jar"]