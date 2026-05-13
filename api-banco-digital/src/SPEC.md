# SPEC — API de Banco Digital (Spring Boot)

## Visão Geral

API REST para um banco digital simplificado, implementada com Spring Boot.  
Sem interface gráfica — apenas endpoints REST.  
Persistência em memória (H2 in-memory) para simplicidade de execução.

---

## Stack & Dependências

| Item | Valor |
|---|---|
| Linguagem | Java 17+ |
| Framework | Spring Boot 3.x |
| Banco de dados | H2 (in-memory) |
| ORM | Spring Data JPA / Hibernate |
| Build | Maven |
| Validação | Jakarta Validation (spring-boot-starter-validation) |
| Testes | JUnit 5 + Mockito (spring-boot-starter-test) |

### `pom.xml` — dependências obrigatórias
```xml
<dependencies>
  <dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
  </dependency>
  <dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-jpa</artifactId>
  </dependency>
  <dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-validation</artifactId>
  </dependency>
  <dependency>
    <groupId>com.h2database</groupId>
    <artifactId>h2</artifactId>
    <scope>runtime</scope>
  </dependency>
  <dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-test</artifactId>
    <scope>test</scope>
  </dependency>
</dependencies>
```

---

## Estrutura de Pacotes

```
src/main/java/com/banco/digital/
├── BancoDigitalApplication.java
├── controller/
│   └── ContaController.java
├── service/
│   └── ContaService.java
├── repository/
│   └── ContaRepository.java
├── model/
│   └── Conta.java
├── dto/
│   ├── ContaRequestDTO.java
│   ├── ContaResponseDTO.java
│   ├── OperacaoDTO.java
│   └── TransferenciaDTO.java
└── exception/
    ├── ContaNaoEncontradaException.java
    ├── SaldoInsuficienteException.java
    ├── ValorInvalidoException.java
    └── GlobalExceptionHandler.java
```

---

## Modelo de Dados

### Entidade `Conta`

```java
@Entity
public class Conta {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    private String nomeTitular;

    @Column(unique = true, nullable = false)
    private String numeroConta;

    @Column(nullable = false)
    private BigDecimal saldo;
}
```

**Regras do modelo:**
- `id` → gerado automaticamente (auto-increment)
- `nomeTitular` → NOT NULL, NOT BLANK
- `numeroConta` → NOT NULL, UNIQUE
- `saldo` → NOT NULL, nunca pode ser negativo

---

## DTOs

### `ContaRequestDTO` — criação de conta
```
nomeTitular  : String  (obrigatório, não vazio)
numeroConta  : String  (obrigatório, não vazio)
saldo        : BigDecimal (opcional; default 0.00; não pode ser negativo)
```

### `ContaResponseDTO` — resposta de leitura
```
id           : Long
nomeTitular  : String
numeroConta  : String
saldo        : BigDecimal
```

### `OperacaoDTO` — depósito e saque
```
valor        : BigDecimal (obrigatório, > 0)
```

### `TransferenciaDTO` — transferência
```
numeroContaOrigem  : String  (obrigatório)
numeroContaDestino : String  (obrigatório)
valor              : BigDecimal (obrigatório, > 0)
```

---

## Endpoints da API

Base path: `/api/contas`

### 1. Criar Conta
```
POST /api/contas
Body: ContaRequestDTO
Response 201: ContaResponseDTO
```
**Regras:**
- `nomeTitular` não pode ser nulo ou vazio → 400
- `numeroConta` deve ser único → 409 Conflict
- `saldo` inicial não pode ser negativo → 400

---

### 2. Consultar Conta por Número
```
GET /api/contas/numero/{numeroConta}
Response 200: ContaResponseDTO
Response 404: conta não encontrada
```

### 2b. Consultar Conta por ID
```
GET /api/contas/{id}
Response 200: ContaResponseDTO
Response 404: conta não encontrada
```

---

### 3. Depósito
```
POST /api/contas/{numeroConta}/deposito
Body: OperacaoDTO
Response 200: ContaResponseDTO (com saldo atualizado)
```
**Regras:**
- `valor` > 0 → caso contrário 400
- Conta deve existir → caso contrário 404

---

### 4. Saque
```
POST /api/contas/{numeroConta}/saque
Body: OperacaoDTO
Response 200: ContaResponseDTO (com saldo atualizado)
```
**Regras:**
- `valor` > 0 → caso contrário 400
- Conta deve existir → caso contrário 404
- `saldo >= valor` → caso contrário 422 (Saldo insuficiente)

---

### 5. Transferência
```
POST /api/contas/transferencia
Body: TransferenciaDTO
Response 200: mensagem de sucesso (String ou JSON simples)
```
**Regras:**
- Conta origem deve existir → 404
- Conta destino deve existir → 404
- `valor` > 0 → 400
- Conta origem deve ter saldo suficiente → 422
- ⚠️ **A operação DEVE ser atômica**: usar `@Transactional` no service.
  - Se o crédito na conta destino falhar após o débito na origem, o débito deve ser revertido.
  - Nunca debitar sem garantir o crédito.

---

## Mapeamento de Status HTTP

| Situação | HTTP Status |
|---|---|
| Sucesso com retorno | 200 OK |
| Criação bem-sucedida | 201 Created |
| Campo inválido / valor ≤ 0 | 400 Bad Request |
| Conta não encontrada | 404 Not Found |
| Número de conta duplicado | 409 Conflict |
| Saldo insuficiente | 422 Unprocessable Entity |
| Erro interno inesperado | 500 Internal Server Error |

---

## Tratamento de Exceções

Criar `GlobalExceptionHandler` anotado com `@RestControllerAdvice`.

Capturar:
- `ContaNaoEncontradaException` → 404
- `SaldoInsuficienteException` → 422
- `ValorInvalidoException` → 400
- `DataIntegrityViolationException` (numeroConta duplicado) → 409
- `MethodArgumentNotValidException` (validação de bean) → 400

Formato do erro:
```json
{
  "erro": "mensagem descritiva"
}
```

---

## Implementação do Service — Pontos Críticos

### `ContaService`

```java
@Service
@Transactional  // padrão transacional para todos os métodos
public class ContaService { ... }
```

#### `transferir(TransferenciaDTO dto)`
```
1. Buscar contaOrigem pelo numeroConta → lançar ContaNaoEncontradaException se não existir
2. Buscar contaDestino pelo numeroConta → lançar ContaNaoEncontradaException se não existir
3. Validar valor > 0 → lançar ValorInvalidoException se não
4. Validar saldo de contaOrigem >= valor → lançar SaldoInsuficienteException se não
5. contaOrigem.setSaldo(contaOrigem.getSaldo().subtract(valor))
6. contaDestino.setSaldo(contaDestino.getSaldo().add(valor))
7. repository.save(contaOrigem)
8. repository.save(contaDestino)
   → Qualquer exceção nos passos 5-8 reverte toda a transação pelo @Transactional
```

**Por que `@Transactional` resolve o problema:**  
O enunciado alerta que "pode debitar de uma conta e não creditar na outra". Com `@Transactional`, se qualquer passo após o débito lançar exceção, o JPA faz rollback automático, garantindo atomicidade.

---

## Configuração `application.properties`

```properties
spring.datasource.url=jdbc:h2:mem:bancodb
spring.datasource.driver-class-name=org.h2.Driver
spring.datasource.username=sa
spring.datasource.password=
spring.jpa.database-platform=org.hibernate.dialect.H2Dialect
spring.jpa.hibernate.ddl-auto=create-drop
spring.h2.console.enabled=true
spring.h2.console.path=/h2-console
```

---

## Testes Obrigatórios

### Testes de Unidade — `ContaServiceTest`

| Cenário | Método | Resultado Esperado |
|---|---|---|
| Criar conta com dados válidos | `criarConta` | Conta salva, retorna DTO |
| Criar conta com nome vazio | `criarConta` | Lança `ValorInvalidoException` ou Bean Validation |
| Criar conta com saldo negativo | `criarConta` | Lança `ValorInvalidoException` |
| Depositar valor positivo | `depositar` | Saldo aumenta corretamente |
| Depositar valor ≤ 0 | `depositar` | Lança `ValorInvalidoException` |
| Depositar em conta inexistente | `depositar` | Lança `ContaNaoEncontradaException` |
| Sacar valor válido | `sacar` | Saldo diminui corretamente |
| Sacar com saldo insuficiente | `sacar` | Lança `SaldoInsuficienteException` |
| Transferência válida | `transferir` | Origem debitada, destino creditado |
| Transferência com saldo insuficiente | `transferir` | Lança `SaldoInsuficienteException`, nenhum saldo alterado |
| Transferência conta origem inexistente | `transferir` | Lança `ContaNaoEncontradaException` |
| Transferência conta destino inexistente | `transferir` | Lança `ContaNaoEncontradaException` |

### Testes de Integração (opcional, bônus)
- `ContaControllerTest` com `@SpringBootTest` + `MockMvc`
- Cobrir os endpoints POST e GET com status HTTP corretos

---

## Ordem de Implementação para o Agente

Seguir exatamente nesta ordem, validando cada etapa antes de prosseguir:

```
[ ] 1. Criar projeto Spring Boot com as dependências corretas
[ ] 2. Configurar application.properties
[ ] 3. Criar entidade Conta com anotações JPA e validação
[ ] 4. Criar ContaRepository (extends JpaRepository)
[ ] 5. Criar DTOs (Request, Response, Operacao, Transferencia)
[ ] 6. Criar exceções customizadas
[ ] 7. Criar GlobalExceptionHandler
[ ] 8. Implementar ContaService com @Transactional
[ ] 9. Implementar ContaController mapeando todos os endpoints
[ ] 10. Escrever testes unitários do ContaService
[ ] 11. Rodar a aplicação e testar manualmente via curl ou Postman
[ ] 12. Verificar os cenários de erro (conta duplicada, saldo insuficiente, valor inválido)
```

---

## Exemplos de Requisições (para teste manual)

### Criar conta
```bash
curl -X POST http://localhost:8080/api/contas \
  -H "Content-Type: application/json" \
  -d '{"nomeTitular":"João Silva","numeroConta":"001","saldo":100.00}'
```

### Consultar conta
```bash
curl http://localhost:8080/api/contas/numero/001
```

### Depositar
```bash
curl -X POST http://localhost:8080/api/contas/001/deposito \
  -H "Content-Type: application/json" \
  -d '{"valor":50.00}'
```

### Sacar
```bash
curl -X POST http://localhost:8080/api/contas/001/saque \
  -H "Content-Type: application/json" \
  -d '{"valor":30.00}'
```

### Transferir
```bash
curl -X POST http://localhost:8080/api/contas/transferencia \
  -H "Content-Type: application/json" \
  -d '{"numeroContaOrigem":"001","numeroContaDestino":"002","valor":20.00}'
```

---

## Critérios de Aceite

- [ ] Aplicação inicia sem erros
- [ ] POST `/api/contas` cria conta e retorna 201
- [ ] GET `/api/contas/{id}` e `/api/contas/numero/{num}` retornam 200 ou 404
- [ ] Depósito com valor válido atualiza saldo → 200
- [ ] Depósito com valor ≤ 0 retorna 400
- [ ] Saque com saldo suficiente atualiza saldo → 200
- [ ] Saque com saldo insuficiente retorna 422, saldo inalterado
- [ ] Transferência atômica: origem e destino atualizados juntos
- [ ] Transferência com saldo insuficiente: nenhuma conta alterada
- [ ] Número de conta duplicado retorna 409
- [ ] Todos os testes unitários passam (`mvn test`)
