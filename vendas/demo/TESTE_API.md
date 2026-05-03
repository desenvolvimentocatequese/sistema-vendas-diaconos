# Exemplos de Teste da API

## 1. Registrar um novo usuário com role ADMIN

```bash
curl -X POST http://localhost:8080/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "nome": "Admin Sistema",
    "email": "admin@example.com",
    "senha": "admin123",
    "cpf": "12345678901",
    "role": "ADMIN"
  }'
```

**Resposta esperada:**
```json
{
  "id": 1,
  "nome": "Admin Sistema",
  "email": "admin@example.com",
  "cpf": "12345678901",
  "role": "ADMIN",
  "isAtivo": true
}
```

---

## 2. Registrar um novo usuário com role USER_COMUM

```bash
curl -X POST http://localhost:8080/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "nome": "João da Silva",
    "email": "joao@example.com",
    "senha": "joao123",
    "cpf": "98765432100",
    "role": "USER_COMUM"
  }'
```

**Resposta esperada:**
```json
{
  "id": 2,
  "nome": "João da Silva",
  "email": "joao@example.com",
  "cpf": "98765432100",
  "role": "USER_COMUM",
  "isAtivo": true
}
```

---

## 3. Fazer login como ADMIN

```bash
curl -X POST http://localhost:8080/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "admin@example.com",
    "senha": "admin123"
  }'
```

**Resposta esperada:**
```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9.eyJpc3MiOiJ2ZW5kYXMtc3lzdGVtIiwic3ViIjoiYWRtaW5AZXhhbXBsZS5jb20iLCJpYXQiOjE3MTI4NzY2MDAsImV4cCI6MTcxMjk2MzAwMH0.xxx",
  "type": "Bearer",
  "expiresIn": 86400000
}
```

---

## 4. Fazer login como USER_COMUM

```bash
curl -X POST http://localhost:8080/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "joao@example.com",
    "senha": "joao123"
  }'
```

**Resposta esperada:**
```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9.eyJpc3MiOiJ2ZW5kYXMtc3lzdGVtIiwic3ViIjoiam9hb0BleGFtcGxlLmNvbSIsImlhdCI6MTcxMjg3NjYwMCwiZXhwIjoxNzEyOTYzMDAwfQ.xxx",
  "type": "Bearer",
  "expiresIn": 86400000
}
```

---

## 5. Usar token para fazer requisição autenticada

```bash
# Salvar o token de uma das respostas acima
TOKEN="eyJhbGciOiJIUzI1NiJ9.eyJpc3MiOiJ2ZW5kYXMtc3lzdGVtIiwic3ViIjoiYWRtaW5AZXhhbXBsZS5jb20iLCJpYXQiOjE3MTI4NzY2MDAsImV4cCI6MTcxMjk2MzAwMH0.xxx"

# Fazer uma requisição com token
curl -X GET http://localhost:8080/api/produtos \
  -H "Authorization: Bearer $TOKEN"
```

---

## 6. Teste de saúde da API

```bash
curl -X GET http://localhost:8080/auth/health
```

**Resposta esperada:**
```
OK
```

---

## Testando Autorização por Role

### ADMIN pode fazer DELETE
```bash
TOKEN="seu-token-admin"

curl -X DELETE http://localhost:8080/api/produtos/1 \
  -H "Authorization: Bearer $TOKEN"
```

### USER_COMUM NÃO pode fazer DELETE (403 Forbidden)
```bash
TOKEN="seu-token-user-comum"

curl -X DELETE http://localhost:8080/api/produtos/1 \
  -H "Authorization: Bearer $TOKEN"
```
Resposta: `403 Forbidden`

### USER_COMUM pode fazer GET
```bash
TOKEN="seu-token-user-comum"

curl -X GET http://localhost:8080/api/produtos \
  -H "Authorization: Bearer $TOKEN"
```

---

## Teste com Postman

### 1. Environment Variables
Crie uma variável no Postman:
- **base_url**: `http://localhost:8080`
- **token**: deixe vazio inicialmente

### 2. Registrar Usuário
- **Method**: POST
- **URL**: `{{base_url}}/auth/register`
- **Body** (raw JSON):
```json
{
  "nome": "Admin Sistema",
  "email": "admin@example.com",
  "senha": "admin123",
  "cpf": "12345678901",
  "role": "ADMIN"
}
```

### 3. Login
- **Method**: POST
- **URL**: `{{base_url}}/auth/login`
- **Body** (raw JSON):
```json
{
  "email": "admin@example.com",
  "senha": "admin123"
}
```

**Script no Tests (automatizar salvar token):**
```javascript
var jsonData = pm.response.json();
pm.environment.set("token", jsonData.token);
```

### 4. Fazer Requisição com Token
- **Method**: GET
- **URL**: `{{base_url}}/api/produtos`
- **Headers**:
  - Key: `Authorization`
  - Value: `Bearer {{token}}`

---

## Erros Comuns

### Email já cadastrado
```bash
curl -X POST http://localhost:8080/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "nome": "Test",
    "email": "admin@example.com",
    "senha": "test123",
    "cpf": "11111111111",
    "role": "USER_COMUM"
  }'
```

**Resposta (400):**
```
Email já cadastrado
```

---

### CPF já cadastrado
```bash
curl -X POST http://localhost:8080/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "nome": "Test",
    "email": "test@example.com",
    "senha": "test123",
    "cpf": "12345678901",
    "role": "USER_COMUM"
  }'
```

**Resposta (400):**
```
CPF já cadastrado
```

---

### Login com credenciais inválidas
```bash
curl -X POST http://localhost:8080/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "admin@example.com",
    "senha": "senha_errada"
  }'
```

**Resposta (401):**
```json
{
  "error": "Email ou senha inválidos"
}
```

---

## Variáveis do JWT Token (Payload)

Quando decodificar o token JWT (em https://jwt.io), você verá:

```json
{
  "iss": "vendas-system",
  "sub": "admin@example.com",
  "iat": 1712876600,
  "exp": 1712963000
}
```

- **iss**: Emissor do token (vendas-system)
- **sub**: Assunto do token (email do usuário)
- **iat**: Timestamp de emissão
- **exp**: Timestamp de expiração

---

## Notas Importantes

1. **Token Expira em 24 horas** (configurável em `application.properties`)
2. **Senha é encriptada com BCrypt** - nunca é armazenada em plain text
3. **CORS está habilitado** para localhost:3000
4. **H2 Console disponível** em http://localhost:8080/h2-console
5. **Todas as requisições após login precisam incluir o token JWT**

---

**Desenvolvido com Spring Boot 4.0.5 + Java 21 + JWT**
