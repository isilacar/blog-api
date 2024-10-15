FROM maven:3.8.5-openjdk-17-slim
WORKDIR /app
COPY target/blog-api-0.0.1-SNAPSHOT.jar /app/blog-api.jar
ENTRYPOINT ["java","-jar","/app/blog-api.jar"]
MAINTAINER isil-acar
