FROM maven:3.8.4-openjdk-17 AS build
WORKDIR /app
COPY pom.xml .
COPY Airlines-Hub/pom.xml Airlines-Hub/
RUN mvn dependency:go-offline
COPY . .
RUN mvn clean package -pl Airlines-Hub -am -DskipTests
FROM openjdk:17-jdk-slim
ARG JAR_FILE=Airlines-Hub/target/Airlines-Hub-*.jar
COPY --from=build /app/${JAR_FILE} app.jar
EXPOSE 8084
ENTRYPOINT ["java","-jar","/app.jar"]