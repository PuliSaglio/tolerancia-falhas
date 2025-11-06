# 1. FASE DE BUILD
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
COPY failure-simulator failure-simulator


# (Opcional, mas recomendado: baixar dependências para otimizar o cache)
RUN mvn dependency:go-offline -pl imd-travel -am

# 3. Copia o restante do código-fonte (principalmente o src/)
#    A flag -a (all) garante que todos os arquivos sejam copiados
COPY . .

# 4. Executa o build, que agora reconhecerá a estrutura de módulos
RUN mvn clean package -pl imd-travel -am -DskipTests


# 2. FASE DE EXECUÇÃO
FROM eclipse-temurin:21-jdk-alpine

# ATENÇÃO: O nome do JAR é artifactId-version.jar.
# Usamos o curinga '*' para o número da versão (ex: 0.0.1-SNAPSHOT)
ARG JAR_FILE=imd-travel/target/imd-travel-*.jar

# Copia o JAR compilado da fase 'build' para a imagem final
COPY --from=build /app/${JAR_FILE} app.jar

# Porta de serviço
EXPOSE 8081

# Comando de execução
ENTRYPOINT ["java","-jar","/app.jar"]
