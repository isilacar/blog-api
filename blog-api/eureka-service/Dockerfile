FROM maven:3.8.5-openjdk-17-slim
WORKDIR /app
ADD target/eureka-server-0.0.1-SNAPSHOT.jar /app/eureka-service.jar
ENTRYPOINT ["java","-jar","/app/eureka-service.jar"]
MAINTAINER isil-acar