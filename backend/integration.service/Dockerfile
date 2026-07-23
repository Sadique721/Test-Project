FROM dirainfotech/openjdk:8
ARG JAR_FILE=build/libs/SavbillIntegrationSystem-0.0.1-SNAPSHOT.jar
COPY ${JAR_FILE} SavbillIntegrationSystem.jar
EXPOSE 41080
ENTRYPOINT ["java", "-jar", "/SavbillIntegrationSystem.jar"]
