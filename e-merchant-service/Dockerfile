FROM maven:3.9.5-eclipse-temurin-17 AS maven
LABEL MAINTAINER="kirilraykov@gmail.com"

WORKDIR /opt
COPY . /opt

# Compile and package the application to an executable JAR
RUN mvn install

FROM eclipse-temurin:17-jdk-jammy

ARG JAR_FILE=e-merchant-app-0.0.1-SNAPSHOT.jar

WORKDIR /opt

# Copy the spring-boot-api-tutorial.jar from the maven stage to the /opt/app directory of the current stage.
COPY --from=maven /opt/target/${JAR_FILE} /opt

ENTRYPOINT ["java","-jar","e-merchant-app-0.0.1-SNAPSHOT.jar"]
