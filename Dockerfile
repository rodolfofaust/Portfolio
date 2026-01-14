# Use a imagem base do OpenJDK 17
FROM openjdk:17-jdk-slim

# Defina o diretório de trabalho
WORKDIR /app

# Copie o arquivo JAR da aplicação
COPY target/ai-image-generator-api-1.0.0.jar app.jar

# Exponha a porta 8080
EXPOSE 8080

# Defina variáveis de ambiente
ENV JAVA_OPTS="-Xmx512m -Xms256m"

# Comando para executar a aplicação
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"] 