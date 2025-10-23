FROM maven:3.8.4-openjdk-17 AS build
WORKDIR /app
COPY pom.xml .
COPY Fidelity/pom.xml Fidelity/
RUN mvn dependency:go-offline
COPY . .
RUN mvn clean package -pl Fidelity -am -DskipTests
FROM openjdk:17-jdk-slim
ARG JAR_FILE=Fidelity/target/Fidelity-*.jar
COPY --from=build /app/${JAR_FILE} app.jar
EXPOSE 8082
ENTRYPOINT ["java","-jar","/app.jar"]