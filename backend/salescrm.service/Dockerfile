FROM dirainfotech/openjdk:8
ARG JAR_FILE=build/libs/SalesCrmsBss-0.0.1-SNAPSHOT.jar 
COPY ${JAR_FILE} salesCrmsBss.jar
RUN mkdir -p /var/document/leaddoc
RUN mkdir -p //var/document/quotationpodocpath
RUN mkdir -p //var/document/leadquotation/
EXPOSE 9093
ENTRYPOINT ["java", "-jar", "/salesCrmsBss.jar"]