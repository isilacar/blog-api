FROM maven:3.8.5-openjdk-17-slim
WORKDIR /app
COPY target/blog-service-0.0.1-SNAPSHOT.jar /app/blog-service.jar
ENTRYPOINT ["java","-jar","/app/blog-service.jar"]
MAINTAINER isil-acar
