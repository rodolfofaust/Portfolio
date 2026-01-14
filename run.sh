#!/bin/bash

# Script para executar a API de Geração de Imagens com IA

echo "🎨 API de Geração de Imagens com IA"
echo "=================================="

# Verificar se o Java está instalado
if ! command -v java &> /dev/null; then
    echo "❌ Java não está instalado. Por favor, instale o Java 17 ou superior."
    exit 1
fi

# Verificar se o Maven está instalado
if ! command -v mvn &> /dev/null; then
    echo "❌ Maven não está instalado. Por favor, instale o Maven 3.6+."
    exit 1
fi

# Verificar se a chave da OpenAI está configurada
if [ -z "$OPENAI_API_KEY" ]; then
    echo "⚠️  Variável OPENAI_API_KEY não está configurada."
    echo "   Configure a variável de ambiente ou crie um arquivo .env"
    echo "   export OPENAI_API_KEY=sua-chave-da-openai-aqui"
    echo ""
    read -p "Deseja continuar mesmo assim? (y/N): " -n 1 -r
    echo
    if [[ ! $REPLY =~ ^[Yy]$ ]]; then
        exit 1
    fi
fi

echo "🔧 Compilando o projeto..."
mvn clean install -DskipTests

if [ $? -ne 0 ]; then
    echo "❌ Erro na compilação. Verifique os erros acima."
    exit 1
fi

echo "✅ Compilação concluída com sucesso!"
echo ""
echo "🚀 Iniciando a aplicação..."
echo "   Acesse: http://localhost:8080"
echo "   Swagger UI: http://localhost:8080/swagger-ui.html"
echo "   H2 Console: http://localhost:8080/h2-console"
echo "   Health Check: http://localhost:8080/api/v1/images/health"
echo ""
echo "Pressione Ctrl+C para parar a aplicação"
echo ""

# Executar a aplicação
mvn spring-boot:run 