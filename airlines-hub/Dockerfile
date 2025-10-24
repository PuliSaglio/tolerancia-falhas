FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app

# 1. Copia o POM Pai
COPY pom.xml .

# 2. Copia TODAS as pastas de módulos e seus pom.xml
#    Para que a pasta 'exchange' exista no /app
COPY imd-travel imd-travel
COPY fidelity fidelity
COPY exchange exchange
COPY airlines-hub airlines-hub

# (Opcional, mas recomendado: baixar dependências para otimizar o cache)
RUN mvn dependency:go-offline -pl airlines-hub -am

# 3. Copia o restante do código-fonte (principalmente o src/)
#    A flag -a (all) garante que todos os arquivos sejam copiados
COPY . .

# 4. Executa o build, que agora reconhecerá a estrutura de módulos
RUN mvn clean package -pl airlines-hub -am -DskipTests
FROM openjdk:21-jdk-slim
ARG JAR_FILE=airlines-hub/target/airlines-hub-*.jar
COPY --from=build /app/${JAR_FILE} app.jar
EXPOSE 8084
ENTRYPOINT ["java","-jar","/app.jar"]