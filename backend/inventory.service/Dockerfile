FROM dirainfotech/openjdk:8
ARG JAR_FILE=build/libs/Savbillinventorymanagement-0.0.1-SNAPSHOT.jar
COPY ${JAR_FILE} Savbillinventorymanagement.jar
EXPOSE 30083
ENTRYPOINT ["java", "-jar", "/Savbillinventorymanagement.jar"]
