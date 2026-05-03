# 📐 Diagrama da Arquitetura - Sistema de Autenticação JWT

## Fluxo de Autenticação Completo

```
╔════════════════════════════════════════════════════════════════════════════╗
║                         CLIENTE (Frontend/Postman)                         ║
╚════════════════════════════════════════════════════════════════════════════╝
                                       │
                   ┌───────────────────┼───────────────────┐
                   ▼                   ▼                   ▼
            ┌────────────────┐ ┌──────────────┐ ┌──────────────────┐
            │ 1. REGISTRO    │ │ 2. LOGIN     │ │ 3. USAR TOKEN    │
            │ POST /register │ │ POST /login  │ │ Authorization    │
            └────────────────┘ └──────────────┘ └──────────────────┘
                   │                   │                   │
                   ▼                   ▼                   ▼
╔════════════════════════════════════════════════════════════════════════════╗
║                      SPRING BOOT APPLICATION                              ║
║ ────────────────────────────────────────────────────────────────────────── ║
│                                                                            │
│  ┌─────────────────────────────────────────────────────────────────────┐  │
│  │                    UsuarioController                               │  │
│  │  @PostMapping("/register")  @PostMapping("/login")                 │  │
│  │      │                            │                                │  │
│  │      └────────────────────────────┼────────────────┐               │  │
│  └───────────────────────────────────┼────────────────┼───────────────┘  │
│                                      ▼                ▼                   │
│  ┌─────────────────────────────────────────────────────────────────────┐  │
│  │                      UsuarioService                                │  │
│  │                                                                    │  │
│  │  registrar(dados)          autenticar(email, senha)                │  │
│  │    │                          │                                    │  │
│  │    ├─ Valida email único       ├─ AuthenticationManager.authenticate│  │
│  │    ├─ Valida CPF único         ├─ Busca usuário                     │  │
│  │    ├─ Encripta (BCrypt)        ├─ Compara senhas                    │  │
│  │    └─ Salva BD                 └─ Chama TokenService.generateToken  │  │
│  │       │                             │                               │  │
│  │       └─────────────────┬───────────┘                               │  │
│  └───────────────────────────────────────────────────────────────────┘  │
│                            │                                             │
│                            ▼                                             │
│  ┌─────────────────────────────────────────────────────────────────────┐  │
│  │                     TokenService                                   │  │
│  │                                                                    │  │
│  │  generateToken(usuario)          validateToken(token)             │  │
│  │    │                               │                              │  │
│  │    ├─ Cria JWT.builder()          ├─ Jwts.parser()               │  │
│  │    ├─ Assina com chave secreta    ├─ Extrai email (subject)      │  │
│  │    ├─ Define expiração (24h)      └─ Valida assinatura           │  │
│  │    └─ Retorna token                                               │  │
│  │       │                                                            │  │
│  │       └────────────────────────────────────────────────────────┐  │  │
│  └───────────────────────────────────────────────────────────────┼──┘  │
│                                                                   │      │
│  ┌────────────────────────────────────────────────────────────────────┐  │
│  │              UsuarioRepository                                     │  │
│  │              extends JpaRepository<UsuarioModel, Long>            │  │
│  │                                                                   │  │
│  │  - findByEmail(String email) → UserDetails                        │  │
│  │  - findByEmailOptional(String email) → Optional<UsuarioModel>     │  │
│  │  - findByCpf(String cpf) → Optional<UsuarioModel>                 │  │
│  └────────────────────────────────────────────────────────────────────┘  │
│           │                                                              │
│           ▼                                                              │
│  ┌────────────────────────────────────────────────────────────────────┐  │
│  │                 UsuarioModel (Entity)                             │  │
│  │   id, nome, email, senha, cpf, role, isAtivo                      │  │
│  │   implements UserDetails                                          │  │
│  │   - getAuthorities() → retorna roles                              │  │
│  └────────────────────────────────────────────────────────────────────┘  │
│           │                                                              │
│           ▼                                                              │
│  ┌────────────────────────────────────────────────────────────────────┐  │
│  │              H2 DATABASE (desenvolvimento)                         │  │
│  │                                                                   │  │
│  │  TABLE usuario                                                    │  │
│  │  ┌──────┬───────┬─────────────────┬───────┬─────┬─────┬──────┐   │  │
│  │  │ id   │ nome  │ email           │ senha │ cpf │role │ ativo│   │  │
│  │  ├──────┼───────┼─────────────────┼───────┼─────┼─────┼──────┤   │  │
│  │  │ 1    │ Admin │admin@email.com  │ ... │ 123...│ADMIN│ true │   │  │
│  │  │ 2    │ João  │joao@email.com   │ ... │ 456...│USER │ true │   │  │
│  │  └──────┴───────┴─────────────────┴───────┴─────┴─────┴──────┘   │  │
│  └────────────────────────────────────────────────────────────────────┘  │
│                                                                            │
║ ────────────────────────────────────────────────────────────────────────── ║
║                    SECURITY LAYER (Filtros)                              ║
║ ────────────────────────────────────────────────────────────────────────── ║
│                                                                            │
│  ┌──────────────────────────────────────────────────────────────────────┐ │
│  │  SecurityFilter (OncePerRequestFilter)                             │ │
│  │                                                                     │ │
│  │  doFilterInternal()                                                │ │
│  │    │                                                               │ │
│  │    ├─ Extrai token do header "Authorization: Bearer <token>"      │ │
│  │    ├─ Valida token com TokenService.validateToken()               │ │
│  │    ├─ Busca usuário com UsuarioRepository.findByEmail()           │ │
│  │    ├─ Cria UsernamePasswordAuthenticationToken                    │ │
│  │    └─ Define SecurityContext                                      │ │
│  │                                                                     │ │
│  └──────────────────────────────────────────────────────────────────────┘ │
│           │                                                                │
│           ▼                                                                │
│  ┌──────────────────────────────────────────────────────────────────────┐ │
│  │  SecurityConfiguration                                             │ │
│  │                                                                     │ │
│  │  authorizeHttpRequests()                                           │ │
│  │    ├─ /auth/** → .permitAll()                                      │ │
│  │    ├─ DELETE /api/** → .hasRole("ADMIN")                           │ │
│  │    ├─ POST|PUT|PATCH /api/** → .hasAnyRole("ADMIN", "USER_COMUM")  │ │
│  │    └─ GET /api/** → .authenticated()                               │ │
│  │                                                                     │ │
│  │  PasswordEncoder → BCryptPasswordEncoder                           │ │
│  │  AuthenticationManager → gerencia autenticação                     │ │
│  │                                                                     │ │
│  └──────────────────────────────────────────────────────────────────────┘ │
│                                                                            │
║ ────────────────────────────────────────────────────────────────────────── ║
║                         CorsConfig                                        ║
║ ────────────────────────────────────────────────────────────────────────── ║
│  Permite requisições do frontend (localhost:3000)                          │
│  Métodos: GET, POST, PUT, DELETE, PATCH, OPTIONS                          │
│                                                                            │
╚════════════════════════════════════════════════════════════════════════════╝
                                       │
                   ┌───────────────────┴───────────────────┐
                   ▼                                       ▼
        ┌────────────────────┐              ┌──────────────────────┐
        │ 201 Usuário criado │              │ 200 Token JWT        │
        │                    │              │ {token, type, exIn}  │
        │ { id, nome, email} │              │                      │
        └────────────────────┘              └──────────────────────┘
                                                         │
                                                         ▼
                                            Requisição com token
                                            Authorization: Bearer...
                                                         │
                                                         ▼
                                            Validado por SecurityFilter
                                                         │
                                                         ▼
                                            ✅ Se válido: Processa
                                            ❌ Se inválido: 401/403
```

---

## Componentes por Camada

### 🎯 Camada de Apresentação (Controller)
```
UsuarioController
├── POST /auth/register → RegisterRequestDTO
├── POST /auth/login → LoginRequestDTO
└── GET /auth/health → String "OK"
```

### 💼 Camada de Negócio (Service)
```
UsuarioService
├── registrar(RegisterRequestDTO) → UsuarioModel
└── autenticar(email, senha) → String (token)

TokenService
├── generateToken(UsuarioModel) → String
└── validateToken(String) → String (email)
```

### 🗄️ Camada de Dados (Repository)
```
UsuarioRepository
├── findByEmail(String) → UserDetails
├── findByEmailOptional(String) → Optional<UsuarioModel>
└── findByCpf(String) → Optional<UsuarioModel>
```

### 🔐 Camada de Segurança (Security)
```
SecurityConfiguration
├── securityFilterChain()
├── authenticationManager()
└── passwordEncoder()

SecurityFilter
└── doFilterInternal()

CorsConfig
└── corsConfigurationSource()
```

### 📦 Modelos (Entity/DTO)
```
UsuarioModel (Entity)
├── id, nome, email, senha, cpf, role, isAtivo
└── implements UserDetails

UsuarioRole (Enum)
├── ADMIN
└── USER_COMUM

DTOs
├── LoginRequestDTO
├── RegisterRequestDTO
└── AuthResponseDTO
```

---

## Fluxo de Requisição HTTP

### Requisição 1: Registro
```
CLIENT                          SPRING BOOT
  │                                 │
  ├─ POST /auth/register ──────────>│
  │  {nome, email, senha, cpf,role} │
  │                                 │
  │                        UsuarioController
  │                                 │
  │                        UsuarioService.registrar()
  │                             │
  │                    ├─ BCryptPasswordEncoder
  │                    └─ UsuarioRepository.save()
  │                             │
  │                        H2 DATABASE
  │                             │
  │<────────── 201 Created ──────┤
  │   {id, nome, email, cpf,    │
  │    role, isAtivo}           │
  │                             │
```

### Requisição 2: Login
```
CLIENT                          SPRING BOOT
  │                                 │
  ├─ POST /auth/login ─────────────>│
  │  {email, senha}                 │
  │                                 │
  │                        UsuarioController
  │                                 │
  │                        UsuarioService.autenticar()
  │                             │
  │                    ├─ AuthenticationManager
  │                    └─ TokenService.generateToken()
  │                        ├─ Jwts.builder()
  │                        ├─ Sign com chave secreta
  │                        └─ Retorna JWT
  │                             │
  │<────────── 200 OK ──────────┤
  │   {token: "eyJh...",       │
  │    type: "Bearer",          │
  │    expiresIn: 86400000}     │
  │                             │
```

### Requisição 3: Requisição Autenticada
```
CLIENT                          SPRING BOOT
  │                                 │
  ├─ GET /api/produtos ────────────>│
  │  Authorization: Bearer eyJh...   │
  │                                 │
  │                        SecurityFilter
  │                             │
  │                    ├─ Extrai token
  │                    ├─ TokenService.validateToken()
  │                    ├─ UsuarioRepository.findByEmail()
  │                    └─ SecurityContext.setAuthentication()
  │                             │
  │                    SecurityConfiguration
  │                        verifica:
  │                    ├─ HTTP Method (GET → ✅)
  │                    ├─ Role (USER_COMUM → ✅)
  │                    └─ Endpoint (/api/produtos → ✅)
  │                             │
  │<────────── 200 OK ──────────┤
  │   {dados da requisição}     │
  │                             │
```

---

## Estrutura de Diretórios Criada

```
com/vendas/system/
│
├── configuration/
│   └── CorsConfig.java                    [Configuração CORS]
│
├── controller/
│   └── UsuarioController.java             [Endpoints REST]
│       ├── @PostMapping("/register")
│       ├── @PostMapping("/login")
│       └── @GetMapping("/health")
│
├── dto/
│   ├── LoginRequestDTO.java               [DTO para login]
│   ├── RegisterRequestDTO.java            [DTO para registro]
│   └── AuthResponseDTO.java               [DTO de resposta JWT]
│
├── infra/
│   └── security/
│       ├── SecurityConfiguration.java     [Config Spring Security]
│       └── SecurityFilter.java            [Filtro JWT]
│
├── model/
│   ├── UsuarioModel.java                  [Entity JPA]
│   └── UsuarioRole.java                   [Enum de roles]
│
├── repository/
│   └── UsuarioRepository.java             [JPA Repository]
│
├── service/
│   ├── TokenService.java                  [Serviço JWT]
│   └── UsuarioService.java                [Lógica de negócio]
│
└── DemoApplication.java                   [Main Application]
```

---

**Desenvolvido com Spring Boot 4.0.5 + Java 21**
