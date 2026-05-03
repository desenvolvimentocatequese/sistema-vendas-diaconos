# 🔐 Sistema de Vendas - Autenticação JWT

Uma implementação completa de autenticação e autorização com **JWT (JSON Web Token)** usando **Spring Boot 4.0.5** e **Java 21**.

## 📦 O que foi criado

### Models & Enums
- ✅ `UsuarioRole.java` - Enum com roles ADMIN e USER_COMUM
- ✅ `UsuarioModel.java` - Entity que implementa UserDetails

### DTOs (Data Transfer Objects)
- ✅ `LoginRequestDTO.java` - Dados para login
- ✅ `RegisterRequestDTO.java` - Dados para registro
- ✅ `AuthResponseDTO.java` - Resposta de autenticação com token

### Repository
- ✅ `UsuarioRepository.java` - JPA Repository com queries customizadas

### Services
- ✅ `TokenService.java` - Geração e validação de JWT tokens
- ✅ `UsuarioService.java` - Lógica de negócio (registro, autenticação)

### Controller
- ✅ `UsuarioController.java` - Endpoints `/auth/register`, `/auth/login`, `/auth/health`

### Security
- ✅ `SecurityConfiguration.java` - Configuração do Spring Security
- ✅ `SecurityFilter.java` - Filtro que valida JWT em cada requisição
- ✅ `CorsConfig.java` - Configuração de CORS

### Configurações
- ✅ `application.properties` - Propriedades da aplicação (JWT, DB, etc)

## 🚀 Quick Start

### 1. Compilar
```bash
./mvnw clean compile
```

### 2. Empacotar
```bash
./mvnw clean package -DskipTests
```

### 3. Executar
```bash
./mvnw spring-boot:run
```

A aplicação estará disponível em: **http://localhost:8080**

## 📋 Endpoints da API

### Registro
```bash
POST /auth/register
Content-Type: application/json

{
  "nome": "João Silva",
  "email": "joao@example.com",
  "senha": "senha123",
  "cpf": "12345678901",
  "role": "USER_COMUM"
}
```

### Login
```bash
POST /auth/login
Content-Type: application/json

{
  "email": "joao@example.com",
  "senha": "senha123"
}

Response:
{
  "token": "eyJhbGciOiJIUzI1NiJ9...",
  "type": "Bearer",
  "expiresIn": 86400000
}
```

### Usar Token
```bash
GET /api/qualquer-recurso
Authorization: Bearer eyJhbGciOiJIUzI1NiJ9...
```

## 🔑 Roles e Permissões

### ADMIN
- ✅ GET, POST, PUT, PATCH, DELETE
- Acesso completo a todos os recursos

### USER_COMUM
- ✅ GET (leitura)
- ✅ POST, PUT, PATCH (edição própria)
- ❌ DELETE (deletar dados)

## 🛢️ Database

Por padrão, usa **H2 em memória**. Para acessar:

**URL:** http://localhost:8080/h2-console
- **JDBC URL:** `jdbc:h2:mem:testdb`
- **User:** `sa`
- **Password:** (vazio)

## 📚 Documentação Completa

- [AUTENTICACAO.md](AUTENTICACAO.md) - Guia detalhado de autenticação
- [TESTE_API.md](TESTE_API.md) - Exemplos de testes com curl e Postman
- [ARQUITETURA.md](ARQUITETURA.md) - Arquitetura e fluxos

## 🔐 Segurança

### ✅ Implementado
- Encriptação de senha com **BCrypt**
- JWT com **expiração de 24 horas**
- **Spring Security** integrado
- **CORS** configurado
- Validação de entrada

### ⚠️ Recomendações Futuras
- HTTPS/TLS
- Rate limiting
- Refresh tokens
- Auditoria de login
- 2FA
- Logout com blacklist

## 📦 Dependências Adicionadas

```xml
<!-- JWT -->
<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt-api</artifactId>
    <version>0.12.5</version>
</dependency>

<!-- Validation -->
<dependency>
    <groupId>jakarta.validation</groupId>
    <artifactId>jakarta.validation-api</artifactId>
</dependency>
```

## 🎯 Próximos Passos

1. Implementar refresh tokens
2. Adicionar auditoria e logs
3. Integrar com banco de dados (PostgreSQL/MySQL)
4. Implementar confirmação de email
5. Adicionar recuperação de senha
6. Implementar 2FA

## 📊 Estrutura de Arquivos

```
src/main/java/com/vendas/system/
├── configuration/
│   └── CorsConfig.java
├── controller/
│   └── UsuarioController.java
├── dto/
│   ├── AuthResponseDTO.java
│   ├── LoginRequestDTO.java
│   └── RegisterRequestDTO.java
├── infra/
│   └── security/
│       ├── SecurityConfiguration.java
│       └── SecurityFilter.java
├── model/
│   ├── UsuarioModel.java
│   └── UsuarioRole.java
├── repository/
│   └── UsuarioRepository.java
├── service/
│   ├── TokenService.java
│   └── UsuarioService.java
└── DemoApplication.java
```

## 🧪 Teste Rápido

### 1. Registre um usuário
```bash
curl -X POST http://localhost:8080/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "nome": "Admin",
    "email": "admin@example.com",
    "senha": "admin123",
    "cpf": "12345678901",
    "role": "ADMIN"
  }'
```

### 2. Faça login
```bash
curl -X POST http://localhost:8080/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "admin@example.com",
    "senha": "admin123"
  }'
```

### 3. Use o token
```bash
TOKEN="seu-token-aqui"

curl -H "Authorization: Bearer $TOKEN" \
  http://localhost:8080/api/qualquer-endpoint
```

## ✅ Status

- [x] Models criados (UsuarioRole, UsuarioModel)
- [x] DTOs criados (Login, Register, AuthResponse)
- [x] Repository implementado
- [x] Services implementados
- [x] Controller implementado
- [x] Security Configuration implementada
- [x] JWT Token Service implementado
- [x] Filtro de segurança implementado
- [x] CORS configurado
- [x] Documentação completa
- [x] Compilação sucesso ✅
- [x] Package sucesso ✅

## 🤝 Contribuições

Este projeto foi criado como base para o sistema de vendas da empresa CCB. Sinta-se livre para estender com novas funcionalidades.

## 📝 Licença

Proprietary - Sistema de Vendas CCB

---

**Desenvolvido com ❤️ usando Spring Boot 4.0.5 + Java 21 + JWT**

**Data:** 11 de Abril de 2026

**Status:** ✅ Pronto para Desenvolvimento
