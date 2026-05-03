# Estrutura do Projeto - Sistema de Vendas

## Arquitetura de Segurança

```
com.vendas.system
├── DemoApplication.java (Main)
│
├── model/
│   ├── UsuarioRole.java (ENUM: ADMIN, USER_COMUM)
│   └── UsuarioModel.java (Entity: implementa UserDetails)
│
├── dto/
│   ├── LoginRequestDTO.java (email, senha)
│   ├── RegisterRequestDTO.java (nome, email, senha, cpf, role)
│   └── AuthResponseDTO.java (token, type, expiresIn)
│
├── repository/
│   └── UsuarioRepository.java (JPA Repository)
│
├── service/
│   ├── TokenService.java (Gera e valida JWT)
│   └── UsuarioService.java (Lógica de negócio)
│
├── controller/
│   └── UsuarioController.java (/auth/register, /auth/login, /auth/health)
│
├── infra/
│   └── security/
│       ├── SecurityConfiguration.java (Spring Security Config)
│       └── SecurityFilter.java (Filtro JWT)
│
└── configuration/
    └── CorsConfig.java (Configuração CORS)
```

---

## Fluxo de Autenticação

```
┌─────────────────────────────────────────────────────────────────┐
│                         REGISTRO DE USUÁRIO                      │
├─────────────────────────────────────────────────────────────────┤
│ POST /auth/register                                              │
│ { nome, email, senha, cpf, role }                               │
│           ↓                                                      │
│  UsuarioController.registrar()                                   │
│           ↓                                                      │
│  UsuarioService.registrar()                                      │
│           ├─ Valida email único                                  │
│           ├─ Valida CPF único                                    │
│           ├─ Encripta senha com BCrypt                           │
│           └─ Salva no banco                                      │
│           ↓                                                      │
│  Retorna 201 Created com usuário criado                          │
└─────────────────────────────────────────────────────────────────┘

┌─────────────────────────────────────────────────────────────────┐
│                         LOGIN DO USUÁRIO                         │
├─────────────────────────────────────────────────────────────────┤
│ POST /auth/login                                                 │
│ { email, senha }                                                 │
│           ↓                                                      │
│  UsuarioController.login()                                       │
│           ↓                                                      │
│  UsuarioService.autenticar()                                     │
│           ├─ Autentica com AuthenticationManager                 │
│           ├─ Busca usuário por email                             │
│           └─ Compara senhas com BCrypt                           │
│           ↓                                                      │
│  TokenService.generateToken()                                    │
│           ├─ Cria JWT com claims                                 │
│           ├─ Assina com chave secreta                            │
│           └─ Define expiração (24h)                              │
│           ↓                                                      │
│  Retorna 200 OK com token JWT                                    │
└─────────────────────────────────────────────────────────────────┘

┌─────────────────────────────────────────────────────────────────┐
│                    REQUISIÇÃO AUTENTICADA                        │
├─────────────────────────────────────────────────────────────────┤
│ GET /api/produtos                                                │
│ Header: Authorization: Bearer <token>                            │
│           ↓                                                      │
│  SecurityFilter.doFilterInternal()                               │
│           ├─ Extrai token do header                              │
│           ├─ Valida token com TokenService                       │
│           ├─ Busca usuário por email                             │
│           └─ Define SecurityContext com usuário                  │
│           ↓                                                      │
│  SecurityConfiguration verifica authorizations                   │
│           ├─ Método HTTP (GET, POST, DELETE, etc)               │
│           ├─ Role do usuário (ADMIN, USER_COMUM)                │
│           └─ Aprova ou nega acesso                               │
│           ↓                                                      │
│  Se autorizado: Requisição processada                            │
│  Se negado: Retorna 403 Forbidden                                │
└─────────────────────────────────────────────────────────────────┘
```

---

## Tabela de Banco de Dados

### Tabela `usuario`

```sql
CREATE TABLE usuario (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    nome VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    senha VARCHAR(255) NOT NULL,
    cpf VARCHAR(11) NOT NULL UNIQUE,
    role VARCHAR(50) NOT NULL,
    is_ativo BOOLEAN DEFAULT TRUE
);
```

---

## Configuração de Autorização por Role

### ADMIN
```
GET    /api/**              ✅ Permitido
POST   /api/**              ✅ Permitido
PUT    /api/**              ✅ Permitido
PATCH  /api/**              ✅ Permitido
DELETE /api/**              ✅ Permitido
```

### USER_COMUM
```
GET    /api/**              ✅ Permitido
POST   /api/**              ✅ Permitido
PUT    /api/**              ✅ Permitido
PATCH  /api/**              ✅ Permitido
DELETE /api/**              ❌ Negado (403)
```

### Sem Autenticação
```
POST   /auth/register       ✅ Permitido
POST   /auth/login          ✅ Permitido
GET    /auth/health         ✅ Permitido
GET    /h2-console/**       ✅ Permitido
GET    /actuator/health     ✅ Permitido
```

---

## Estrutura do JWT Token

### Header
```json
{
  "alg": "HS256",
  "typ": "JWT"
}
```

### Payload
```json
{
  "iss": "vendas-system",
  "sub": "usuario@example.com",
  "iat": 1712876600,
  "exp": 1712963000
}
```

### Assinatura
```
HMACSHA256(
  base64UrlEncode(header) + "." +
  base64UrlEncode(payload),
  secret
)
```

---

## Dependências Principais

```xml
<!-- Spring Boot -->
<spring-boot>4.0.5</spring-boot>

<!-- Spring Security -->
<spring-security>6.2.x</spring-security>

<!-- Spring Data JPA -->
<spring-data-jpa>3.2.x</spring-data-jpa>

<!-- JWT -->
<jjwt>0.12.5</jjwt>

<!-- Database -->
<h2>2.1.x</h2>

<!-- Validation -->
<jakarta.validation>3.0.x</jakarta.validation>
```

---

## Configurações Importantes

### application.properties
```properties
# JWT
api.security.token.secret=<sua-chave-secreta>
api.security.token.expiration=86400000  # 24 horas

# CORS
app.frontend.url=http://localhost:3000

# Banco
spring.jpa.hibernate.ddl-auto=update
spring.h2.console.enabled=true
```

---

## Segurança em Produção

### ✅ Já Implementado
- [x] Encriptação de senha com BCrypt
- [x] JWT com expiração
- [x] Spring Security integrado
- [x] CORS configurado
- [x] Validação de entrada (annotations)

### ⚠️ Recomendado Adicionar
- [ ] HTTPS/TLS
- [ ] Rate limiting
- [ ] Refresh tokens
- [ ] Auditoria de login
- [ ] 2FA (Two Factor Authentication)
- [ ] Logout/Blacklist de tokens
- [ ] CSRF token para formulários

---

## Como Rodar o Projeto

### 1. Compilar
```bash
./mvnw clean compile
```

### 2. Executar Testes
```bash
./mvnw test
```

### 3. Empacotar
```bash
./mvnw clean package
```

### 4. Rodar a Aplicação
```bash
./mvnw spring-boot:run
```

Ou:
```bash
java -jar target/demo-0.0.1-SNAPSHOT.jar
```

### 5. Acessar
- API: http://localhost:8080
- H2 Console: http://localhost:8080/h2-console

---

## Próximas Features

1. **Refresh Token** - Renovar token sem fazer login novamente
2. **Logout** - Blacklist de tokens revogados
3. **2FA** - Autenticação de dois fatores
4. **OAuth2** - Login com Google/GitHub
5. **Auditoria** - Log de todas as operações sensíveis
6. **Recuperação de Senha** - Email com link de reset
7. **Confirmação de Email** - Validar email ao registrar

---

**Desenvolvido com ❤️ usando Spring Boot 4.0.5 + Java 21**
