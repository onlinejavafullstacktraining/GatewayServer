FROM openjdk:20-slim as build
ARG JAR_FILE
COPY target/${JAR_FILE} gatewayserver.jar
EXPOSE 5555
ENTRYPOINT ["java", "-jar", "/gatewayserver.jar"]

