# Wallet / Plataforma Bancária Digital

## VÍDEO SOBRE PROJETO

https://youtu.be/KcNkJDCJnZY

Monólito Spring Boot + React cujo domínio inicial é uma carteira digital. O objetivo atual não é reproduzir um banco completo: é entregar uma base pequena e consistente para usuários, carteiras e lançamentos, preparada para evoluir em trabalhos posteriores.

## Objetivo e evolução

- **TP1:** monólito em camadas e API REST para contas e transações.
- **TP2 (estado atual):** persistência JPA/Spring Data, integridade, consultas e histórico auditável.
- **TP3:** separação gradual por módulos e possível extração de transações como microsserviço.
- **Futuro:** autenticação/autorização, observabilidade, antifraude e arquitetura distribuída.

## Design da persistência

```text
User 1 ─── N Wallet 1 ─── N Transaction
                         CREDIT | DEBIT
```

`User`, `Wallet` e `Transaction` são entidades JPA auditadas. As relações usam chaves estrangeiras e carregamento lazy; a API usa DTOs para não expor o grafo de persistência. Todas herdam `createdAt`, `updatedAt` e `version` de `BaseEntity`.

- E-mail possui restrição e índice únicos, além de normalização em minúsculas.
- Valores usam `BigDecimal` com precisão `19,2` e devem ser positivos.
- Tipo, valor e carteira de um lançamento são imutáveis; apenas a descrição pode ser corrigida.
- O saldo não é duplicado em uma coluna: uma consulta agregada soma créditos e subtrai débitos.
- Índices cobrem carteiras por usuário e o histórico de lançamentos por carteira/data e tipo.
- Serviços usam limites transacionais e leituras marcadas como `readOnly`.
- A carteira recebe lock pessimista ao lançar uma transação, serializando débitos concorrentes e protegendo a regra de saldo suficiente. `@Version` também detecta atualizações concorrentes nas entidades.

### Histórico de dados

O Hibernate Envers cria automaticamente `users_aud`, `wallets_aud`, `transactions_aud` e `revinfo`. Cada inclusão, alteração ou exclusão gera uma revisão. O endpoint de histórico traduz os tipos para:

- `ADD`: criação;
- `MOD`: alteração;
- `DEL`: exclusão.

Assim, a tabela operacional continua otimizada para o estado atual, enquanto as tabelas `_aud` preservam a rastreabilidade.

## Repositórios Spring Data

- `UserRepository`: CRUD e busca de existência por e-mail sem diferenciar maiúsculas.
- `WalletRepository`: CRUD, carteiras por usuário e leitura com lock para lançamentos.
- `TransactionRepository`: CRUD, histórico limitado/ordenado e cálculo de saldo via JPQL.

Exemplo de uso dentro de um serviço transacional:

```java
Wallet wallet = walletRepository.findByIdForUpdate(walletId).orElseThrow();
BigDecimal balance = transactionRepository.calculateBalance(walletId);
transactionRepository.save(new Transaction(wallet, TransactionType.DEBIT, amount, description));
```

## API e exemplos

Inicie criando o usuário, depois a carteira e por fim os lançamentos:

```bash
curl -X POST http://localhost:8080/api/users \
  -H 'Content-Type: application/json' \
  -d '{"name":"Ada Lovelace","email":"ada@example.com"}'

curl -X POST http://localhost:8080/api/wallets \
  -H 'Content-Type: application/json' \
  -d '{"userId":1,"currency":"BRL"}'

curl -X POST http://localhost:8080/api/transactions \
  -H 'Content-Type: application/json' \
  -d '{"walletId":1,"type":"CREDIT","amount":150.00,"description":"Depósito"}'

curl http://localhost:8080/api/wallets/1/balance
curl 'http://localhost:8080/api/wallets/1/transactions?limit=50'
curl http://localhost:8080/api/transactions/1/history
```

Outros endpoints:

| Método | Rota | Uso |
|---|---|---|
| `GET` | `/api/users` | Lista usuários |
| `GET`, `PUT` | `/api/users/{id}` | Consulta/atualiza usuário |
| `GET` | `/api/wallets/{id}` | Consulta carteira |
| `GET` | `/api/wallets/user/{userId}` | Carteiras do usuário |
| `GET`, `PATCH`, `DELETE` | `/api/transactions/{id}` | Consulta/corrige descrição/exclui lançamento |

## Execução

### Desenvolvimento rápido com H2 persistente

```bash
cd backend
mvn spring-boot:run
```

Os dados ficam em `backend/data/walletdb`. O console está em `http://localhost:8080/h2-console`, com JDBC URL `jdbc:h2:file:./data/walletdb`.

### PostgreSQL

```bash
docker compose up -d postgres
cd backend
mvn spring-boot:run -Dspring-boot.run.profiles=postgres
```

O perfil aceita `DB_URL`, `DB_USERNAME` e `DB_PASSWORD`. Os valores padrão correspondem ao `docker-compose.yml`.
O PostgreSQL do container é publicado em `localhost:5433` para não conflitar com uma instalação local na porta padrão `5432`.

### Frontend

```bash
cd frontend
npm install
npm run dev
```

## Testes automatizados

```bash
cd backend
mvn test
```

Os testes usam H2 isolado em memória e recriam o schema. A suíte demonstra inicialização do contexto, relacionamentos persistidos, cálculo de saldo, consultas, unicidade de e-mail, bloqueio de saldo insuficiente e criação/consulta de revisões Envers.

## Limites conscientes do escopo

Esta entrega ainda não implementa transferência atômica entre carteiras, autenticação, autorização por proprietário, estorno contábil, idempotência ou migrations versionadas. Em um sistema financeiro real, lançamentos não seriam apagados: seriam compensados por um novo lançamento. Esses pontos pertencem à evolução planejada, não ao MVP acadêmico atual.
