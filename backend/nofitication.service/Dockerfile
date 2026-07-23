FROM dirainfotech/openjdk:8
ARG JAR_FILE=build/libs/SavbillNotification-0.0.1-SNAPSHOT.jar
COPY ${JAR_FILE} savbillNotification.jar
EXPOSE 9092
ENTRYPOINT ["java", "-jar", "/savbillNotification.jar"]