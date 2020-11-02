FROM openjdk:8-jdk-alpine

RUN addgroup -S spring && adduser -S spring -G spring

USER spring:spring

ENV DB_URL=jdbc:postgresql://database-client.cnk2ykjwwh2q.us-east-1.rds.amazonaws.com:5432/postgres
ENV DB_USER=postgres
ENV DB_PASS=6KNnqhZX2dCIPnzQi0uL

ARG JAR_FILE=target/*.jar

COPY ${JAR_FILE} app.jar

ENTRYPOINT ["java", "-Dspring.profiles.active=prod","-jar","/app.jar"]
