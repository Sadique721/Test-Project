FROM dirainfotech/openjdk:8
ARG JAR_FILE=build/libs/SavbillRevenueManagement-0.0.1-SNAPSHOT.jar
COPY ${JAR_FILE} SavbillRevenueManagement.jar
EXPOSE 40080
ENTRYPOINT ["java", "-jar", "/SavbillRevenueManagement.jar"]
