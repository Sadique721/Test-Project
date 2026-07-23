FROM openjdk:8
ARG JAR_FILE=build/libs/SavbillPartnerManagement-0.0.1-SNAPSHOT.jar
COPY ${JAR_FILE} SavbillPartnerManagement.jar
EXPOSE 40081
ENTRYPOINT ["java", "-jar", "/SavbillPartnerManagement.jar"]
