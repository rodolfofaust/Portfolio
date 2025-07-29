# 🎨 API de Geração de Imagens com IA

Uma API REST moderna desenvolvida em Java com Spring Boot para geração de imagens usando Inteligência Artificial através da OpenAI.

## 🚀 Características

- **Geração de Imagens**: Integração com OpenAI DALL-E para criação de imagens
- **API REST**: Endpoints bem documentados com Swagger/OpenAPI
- **Persistência**: Banco de dados H2 com JPA/Hibernate
- **Segurança**: Configuração de CORS e segurança básica
- **Documentação**: Swagger UI integrado
- **Logging**: Sistema de logs detalhado
- **Monitoramento**: Endpoints de health check

## 🛠️ Tecnologias Utilizadas

- **Java 17**
- **Spring Boot 3.2.0**
- **Spring Security**
- **Spring Data JPA**
- **H2 Database**
- **OpenAI API Client**
- **Swagger/OpenAPI 3**
- **Lombok**
- **Maven**

## 📋 Pré-requisitos

- Java 17 ou superior
- Maven 3.6+
- Chave da API da OpenAI

## 🔧 Instalação

1. **Clone o repositório**
   ```bash
   git clone <url-do-repositorio>
   cd ai-image-generator-api
   ```

2. **Configure a chave da OpenAI**
   
   Crie um arquivo `.env` na raiz do projeto:
   ```bash
   OPENAI_API_KEY=sua-chave-da-openai-aqui
   ```
   
   Ou configure a variável de ambiente:
   ```bash
   export OPENAI_API_KEY=sua-chave-da-openai-aqui
   ```

3. **Compile e execute**
   ```bash
   mvn clean install
   mvn spring-boot:run
   ```

## 🌐 Endpoints da API

### Base URL
```
http://localhost:8080/api/v1/images
```

### Endpoints Disponíveis

#### 1. Gerar Imagem
```http
POST /api/v1/images/generate
Content-Type: application/json

{
  "prompt": "Um gato siamês sentado em um jardim japonês",
  "negativePrompt": "borrão, baixa qualidade",
  "size": "1024x1024",
  "style": "vivid",
  "quality": 1,
  "n": 1
}
```

**Resposta:**
```json
{
  "id": "1",
  "prompt": "Um gato siamês sentado em um jardim japonês",
  "negativePrompt": "borrão, baixa qualidade",
  "size": "1024x1024",
  "style": "vivid",
  "quality": 1,
  "imageUrls": [
    "https://oaidalleapiprodscus.blob.core.windows.net/private/..."
  ],
  "createdAt": "2024-01-15T10:30:00",
  "status": "COMPLETED"
}
```

#### 2. Listar Todas as Gerações
```http
GET /api/v1/images
```

#### 3. Buscar Geração por ID
```http
GET /api/v1/images/{id}
```

#### 4. Health Check
```http
GET /api/v1/images/health
```

## 📖 Documentação da API

Acesse a documentação interativa da API:

- **Swagger UI**: http://localhost:8080/swagger-ui.html
- **OpenAPI JSON**: http://localhost:8080/v3/api-docs

## 🗄️ Banco de Dados

- **Tipo**: H2 (em memória)
- **Console**: http://localhost:8080/h2-console
- **JDBC URL**: `jdbc:h2:mem:testdb`
- **Usuário**: `sa`
- **Senha**: `password`

## 🔒 Segurança

- CORS configurado para permitir requisições de qualquer origem
- CSRF desabilitado para APIs REST
- Endpoints públicos para geração de imagens

## 📊 Monitoramento

- **Health Check**: http://localhost:8080/actuator/health
- **Info**: http://localhost:8080/actuator/info
- **Metrics**: http://localhost:8080/actuator/metrics

## 🧪 Testando a API

### Usando cURL

```bash
# Gerar uma imagem
curl -X POST http://localhost:8080/api/v1/images/generate \
  -H "Content-Type: application/json" \
  -d '{
    "prompt": "Uma paisagem de montanha ao pôr do sol",
    "size": "1024x1024",
    "style": "vivid"
  }'

# Listar todas as gerações
curl http://localhost:8080/api/v1/images

# Health check
curl http://localhost:8080/api/v1/images/health
```

### Usando Postman

1. Importe a coleção do Postman (se disponível)
2. Configure a variável `baseUrl` como `http://localhost:8080`
3. Execute as requisições

## 🏗️ Estrutura do Projeto

```
src/
├── main/
│   ├── java/
│   │   └── com/portfolio/aigenerator/
│   │       ├── AiImageGeneratorApplication.java
│   │       ├── controller/
│   │       │   └── ImageGenerationController.java
│   │       ├── service/
│   │       │   ├── ImageGenerationService.java
│   │       │   └── OpenAIService.java
│   │       ├── repository/
│   │       │   └── ImageGenerationRepository.java
│   │       ├── model/
│   │       │   ├── ImageGeneration.java
│   │       │   ├── ImageGenerationRequest.java
│   │       │   └── ImageGenerationResponse.java
│   │       └── config/
│   │           └── SecurityConfig.java
│   └── resources/
│       └── application.yml
└── test/
    └── java/
        └── com/portfolio/aigenerator/
            └── (testes unitários)
```

## 🔧 Configuração Avançada

### Variáveis de Ambiente

| Variável | Descrição | Padrão |
|----------|-----------|---------|
| `OPENAI_API_KEY` | Chave da API da OpenAI | `your-openai-api-key-here` |
| `SERVER_PORT` | Porta do servidor | `8080` |

### Configurações do OpenAI

- **Modelo**: DALL-E 3
- **Tamanhos suportados**: 1024x1024, 1792x1024, 1024x1792
- **Estilos**: vivid, natural
- **Qualidade**: standard, hd

## 🚀 Deploy

### Docker

```dockerfile
FROM openjdk:17-jdk-slim
COPY target/ai-image-generator-api-1.0.0.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app.jar"]
```

### Heroku

```bash
heroku create ai-image-generator-api
heroku config:set OPENAI_API_KEY=sua-chave-aqui
git push heroku main
```

## 🤝 Contribuição

1. Fork o projeto
2. Crie uma branch para sua feature (`git checkout -b feature/AmazingFeature`)
3. Commit suas mudanças (`git commit -m 'Add some AmazingFeature'`)
4. Push para a branch (`git push origin feature/AmazingFeature`)
5. Abra um Pull Request

## 📝 Licença

Este projeto está sob a licença MIT. Veja o arquivo `LICENSE` para mais detalhes.

## 👨‍💻 Autor

**Seu Nome**
- GitHub: [@seu-usuario](https://github.com/seu-usuario)
- LinkedIn: [seu-linkedin](https://linkedin.com/in/seu-linkedin)

## 🙏 Agradecimentos

- OpenAI pela API de geração de imagens
- Spring Boot pela excelente framework
- Comunidade Java por todo o suporte

---

⭐ Se este projeto te ajudou, considere dar uma estrela no repositório! 