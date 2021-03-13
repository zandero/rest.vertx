FROM maven:3.6.0-jdk-11-slim AS build
COPY src /home/app/src
COPY pom.xml /home/app
EXPOSE 4444
RUN mvn -f /home/app/pom.xml clean compile test
