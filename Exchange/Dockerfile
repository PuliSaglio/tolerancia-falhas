FROM maven:3.8.4-openjdk-17 AS build
WORKDIR /app
COPY pom.xml .
COPY Exchange/pom.xml Exchange/
RUN mvn dependency:go-offline
COPY . .
RUN mvn clean package -pl Exchange -am -DskipTests
FROM openjdk:17-jdk-slim
ARG JAR_FILE=Exchange/target/Exchange-*.jar
COPY --from=build /app/${JAR_FILE} app.jar
EXPOSE 8083
ENTRYPOINT ["java","-jar","/app.jar"]