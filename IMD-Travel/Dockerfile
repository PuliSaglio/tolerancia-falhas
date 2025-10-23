# 1. FASE DE BUILD
FROM maven:3.8.4-openjdk-17 AS build
WORKDIR /app
# Copia o pom.xml principal e o pom.xml do módulo IMD-Travel
COPY pom.xml .
COPY IMD-Travel/pom.xml IMD-Travel/
RUN mvn dependency:go-offline

# Copia todo o código-fonte
COPY . .

# Constrói o módulo IMD-Travel
RUN mvn clean package -pl IMD-Travel -am -DskipTests


# 2. FASE DE EXECUÇÃO
FROM openjdk:17-jdk-slim

# ATENÇÃO: O nome do JAR é artifactId-version.jar.
# Usamos o curinga '*' para o número da versão (ex: 0.0.1-SNAPSHOT)
ARG JAR_FILE=IMD-Travel/target/IMD-Travel-*.jar

# Copia o JAR compilado da fase 'build' para a imagem final
COPY --from=build /app/${JAR_FILE} app.jar

# Porta de serviço
EXPOSE 8081

# Comando de execução
ENTRYPOINT ["java","-jar","/app.jar"]