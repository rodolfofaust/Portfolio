# Script PowerShell para executar a API de Geração de Imagens com IA

Write-Host "🎨 API de Geração de Imagens com IA" -ForegroundColor Cyan
Write-Host "==================================" -ForegroundColor Cyan

# Verificar se o Java está instalado
try {
    $javaVersion = java -version 2>&1 | Select-String "version"
    if ($LASTEXITCODE -ne 0) {
        throw "Java não encontrado"
    }
    Write-Host "✅ Java encontrado: $javaVersion" -ForegroundColor Green
} catch {
    Write-Host "❌ Java não está instalado. Por favor, instale o Java 17 ou superior." -ForegroundColor Red
    exit 1
}

# Verificar se o Maven está instalado
try {
    $mavenVersion = mvn -version 2>&1 | Select-String "Apache Maven"
    if ($LASTEXITCODE -ne 0) {
        throw "Maven não encontrado"
    }
    Write-Host "✅ Maven encontrado: $mavenVersion" -ForegroundColor Green
} catch {
    Write-Host "❌ Maven não está instalado. Por favor, instale o Maven 3.6+." -ForegroundColor Red
    exit 1
}

# Verificar se a chave da OpenAI está configurada
if (-not $env:OPENAI_API_KEY) {
    Write-Host "⚠️  Variável OPENAI_API_KEY não está configurada." -ForegroundColor Yellow
    Write-Host "   Configure a variável de ambiente ou crie um arquivo .env" -ForegroundColor Yellow
    Write-Host "   `$env:OPENAI_API_KEY = 'sua-chave-da-openai-aqui'" -ForegroundColor Yellow
    Write-Host ""
    $continue = Read-Host "Deseja continuar mesmo assim? (y/N)"
    if ($continue -ne "y" -and $continue -ne "Y") {
        exit 1
    }
}

Write-Host "🔧 Compilando o projeto..." -ForegroundColor Yellow
mvn clean install -DskipTests

if ($LASTEXITCODE -ne 0) {
    Write-Host "❌ Erro na compilação. Verifique os erros acima." -ForegroundColor Red
    exit 1
}

Write-Host "✅ Compilação concluída com sucesso!" -ForegroundColor Green
Write-Host ""
Write-Host "🚀 Iniciando a aplicação..." -ForegroundColor Green
Write-Host "   Acesse: http://localhost:8080" -ForegroundColor Cyan
Write-Host "   Swagger UI: http://localhost:8080/swagger-ui.html" -ForegroundColor Cyan
Write-Host "   H2 Console: http://localhost:8080/h2-console" -ForegroundColor Cyan
Write-Host "   Health Check: http://localhost:8080/api/v1/images/health" -ForegroundColor Cyan
Write-Host ""
Write-Host "Pressione Ctrl+C para parar a aplicação" -ForegroundColor Yellow
Write-Host ""

# Executar a aplicação
mvn spring-boot:run 