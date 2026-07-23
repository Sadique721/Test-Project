FROM dirainfotech/openjdk:8
ARG JAR_FILE=build/libs/SavbillCommonGateway-0.0.1-SNAPSHOT.jar
COPY ${JAR_FILE} SavbillCommonGateway.jar
EXPOSE 30080
ENTRYPOINT ["java", "-jar", "/SavbillCommonGateway.jar"]
