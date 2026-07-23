# Use Eclipse Temurin JDK 17 base image
FROM eclipse-temurin:17-jdk

# Install SCTP native library
RUN apt-get update && \
    apt-get install -y lksctp-tools libevent-dev && \
    rm -rf /var/lib/apt/lists/*

# Copy your JAR
ARG JAR_FILE=build/libs/diameter-protocol-1.0.0.jar
COPY ${JAR_FILE} /diameter-protocol-1.0.0.jar

# Expose Diameter SCTP port (same as TCP/UDP for reference)
EXPOSE 3869/tcp
EXPOSE 3869/udp
EXPOSE 3869/sctp

EXPOSE 3868/tcp
EXPOSE 3868/udp
EXPOSE 3868/sctp

# Set entrypoint
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","/diameter-protocol-1.0.0.jar"]
