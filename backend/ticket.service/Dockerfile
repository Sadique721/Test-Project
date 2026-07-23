FROM dirainfotech/openjdk:8
ARG JAR_FILE=build/libs/TicketManagement-0.0.1-SNAPSHOT.jar
COPY ${JAR_FILE} TicketManagement.jar
EXPOSE 30081
ENTRYPOINT ["java", "-jar", "/TicketManagement.jar"]
