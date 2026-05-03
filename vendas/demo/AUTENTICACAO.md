# Sistema de Vendas - API de Autenticação e Autorização

## Visão Geral

Este projeto implementa um sistema completo de autenticação e autorização utilizando **JWT (JSON Web Token)** com **Spring Boot 3.x** e **Java 21**.

## Estrutura Criada

### Models
- **UsuarioRole**: Enum com dois papéis: `ADMIN` e `USER_COMUM`
- **UsuarioModel**: Entidade que implementa `UserDetails` do Spring Security

### DTOs
- **LoginRequestDTO**: Requisição de login (email, senha)
- **RegisterRequestDTO**: Requisição de registro (nome, email, senha, cpf, role)
- **AuthResponseDTO**: Resposta de autenticação (token, tipo, tempo de expiração)

### Repository
- **UsuarioRepository**: Interface JPA com métodos customizados para buscar usuários

### Services
- **TokenService**: Gera e valida tokens JWT
- **UsuarioService**: Lógica de negócio para registro e autenticação

### Controller
- **UsuarioController**: Endpoints de `/auth/register` e `/auth/login`

### Segurança
- **SecurityConfiguration**: Configuração do Spring Security com filtro JWT
- **SecurityFilter**: Filtro que valida o token JWT em cada requisição
- **CorsConfig**: Configuração de CORS

## Endpoints da API

### 1. Registrar Novo Usuário
```
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

**Resposta (201 Created):**
```json
{
  "id": 1,
  "nome": "João Silva",
  "email": "joao@example.com",
  "cpf": "12345678901",
  "role": "USER_COMUM",
  "isAtivo": true
}
```

### 2. Fazer Login
```
POST /auth/login
Content-Type: application/json

{
  "email": "joao@example.com",
  "senha": "senha123"
}
```

**Resposta (200 OK):**
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "type": "Bearer",
  "expiresIn": 86400000
}
```

### 3. Health Check
```
GET /auth/health
```

**Resposta (200 OK):**
```
OK
```

## Usando o Token JWT

Após fazer login, você receberá um token. Use-o em suas requisições autenticadas:

```
GET /api/recursos
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
```

## Autorização por Roles

### ADMIN
- Acesso completo a todas as operações (GET, POST, PUT, DELETE, PATCH)
- Herda todas as permissões de USER_COMUM

### USER_COMUM
- Acesso de leitura (GET) em todos os recursos
- Acesso de escrita (POST, PUT, PATCH) em recursos permitidos
- Sem acesso a DELETE

## Configuração

As seguintes propriedades precisam ser configuradas em `application.properties`:

```properties
# JWT Configuration
api.security.token.secret=my-super-secret-key-that-must-be-very-long-to-work-properly-with-hs256-algorithm
api.security.token.expiration=86400000

# Frontend URL
app.frontend.url=http://localhost:3000
```

### Explicação das Propriedades

- **api.security.token.secret**: Chave secreta para assinar tokens JWT (use uma chave forte em produção)
- **api.security.token.expiration**: Tempo de expiração do token em milissegundos (86400000 = 24 horas)
- **app.frontend.url**: URL do frontend para CORS

## Database

Por padrão, o projeto usa **H2 Database** para desenvolvimento. A configuração automática cria as tabelas necessárias.

### Acessar H2 Console
```
http://localhost:8080/h2-console
```

**Credenciais:**
- URL JDBC: `jdbc:h2:mem:testdb`
- User: `sa`
- Password: (deixar vazio)

## Fluxo de Autenticação

```
1. Usuário faz registro em POST /auth/register
2. Senha é encriptada com BCrypt
3. Usuário faz login em POST /auth/login
4. Sistema retorna JWT token
5. Cliente envia token no header Authorization: Bearer <token>
6. SecurityFilter valida o token e autentica o usuário
7. Requisição é processada com autorização apropriada
```

## Validações Implementadas

- ✅ Email único
- ✅ CPF único
- ✅ Validação de formato de email
- ✅ Encriptação de senhas com BCrypt
- ✅ Token JWT com expiração
- ✅ Verificação de ativação de usuário

## Roles e Permissões

```
ADMIN:
  - ROLE_ADMIN
  - ROLE_USER_COMUM

USER_COMUM:
  - ROLE_USER_COMUM
```

## Próximos Passos

1. **Produção**: Substituir a chave JWT por uma chave criptográfica forte
2. **Database**: Migrar para PostgreSQL ou MySQL
3. **Refresh Token**: Implementar refresh token para renovar sessões
4. **2FA**: Adicionar autenticação de dois fatores
5. **Auditoria**: Registrar tentativas de login e operações sensíveis

## Dependências Adicionadas

```xml
<!-- JWT -->
<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt-api</artifactId>
    <version>0.12.5</version>
</dependency>
<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt-impl</artifactId>
    <version>0.12.5</version>
    <scope>runtime</scope>
</dependency>
<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt-jackson</artifactId>
    <version>0.12.5</version>
    <scope>runtime</scope>
</dependency>
```

---

**Desenvolvido com Spring Boot 4.0.5 e Java 21**
