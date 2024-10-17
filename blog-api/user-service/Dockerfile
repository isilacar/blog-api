FROM maven:3.8.5-openjdk-17-slim
WORKDIR /app
COPY target/user-service-0.0.1-SNAPSHOT.jar /app/user-service.jar
ENTRYPOINT ["java","-jar","/app/user-service.jar"]
MAINTAINER isil-acar

