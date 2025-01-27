FROM openjdk:21-jdk-slim
RUN sh mvnw clean package
ARG JAR_FILE=target/microservice-app-0.0.1-SNAPSHOT.jar
COPY ${JAR_FILE} app.jar
ENTRYPOINT ["java", "-jar", "/app.jar"]
