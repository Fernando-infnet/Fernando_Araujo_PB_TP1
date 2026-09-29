# Wallet / Plataforma Bancária Digital

## Vídeo da apresentação

[![Veja a apresentação final da Safe Wallet](https://img.youtube.com/vi/CO__qgEfpoQ/maxresdefault.jpg)](https://youtu.be/CO__qgEfpoQ)

*Clique para acessar o vídeo. A apresentação audiovisual do projeto é centralizada neste README.*

## ENTREGAS TP4 E TP5

- [TP4 — Arquitetura orientada a eventos](TP4/README.md)
- [TP5 — Implantação e manutenção em produção](TP5/README.md)

Cada pasta possui sua documentação e seu checklist técnico. O vídeo é apresentado apenas no topo deste README para evitar links duplicados entre as entregas.

Monólito Spring Boot + React cujo domínio inicial é uma carteira digital. O objetivo atual não é reproduzir um banco completo: é entregar uma base pequena e consistente para usuários, carteiras e lançamentos, preparada para evoluir em trabalhos posteriores.

## Objetivo e evolução

- **TP1:** monólito em camadas e API REST para contas e transações.
- **TP2:** persistência JPA/Spring Data, integridade, consultas e histórico auditável.
- **TP3:** extração das transações para um microsserviço independente.
- **TP4:** comunicação orientada a eventos com RabbitMQ, Outbox, retry, DLQ e idempotência.
- **TP5:** Docker, Kubernetes, observabilidade com Grafana/Prometheus/Loki/Tempo e CI/CD.

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

### Plataforma completa do TP5

```bash
docker compose up -d --build
./scripts/smoke-test.sh
```

A aplicação fica em `http://localhost:3000`. Consulte [TP5/README.md](TP5/README.md) para implantação, operação e demonstração.

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

A interface apresenta saldo, totais e atividade recente, permite registrar créditos, débitos, envios e pagamentos PIX pelas ações rápidas e informa a disponibilidade do microsserviço de transações. Todos os controles da tela principal têm comportamento definido: a navegação leva às seções, o gráfico usa dados reais e permite trocar o período, o seletor alterna entre carteiras, notificações abrem os lançamentos recentes e o perfil pode ser atualizado pela API. O extrato pode ser pesquisado por descrição ou valor, filtrado por tipo, excluído com confirmação e exportado em CSV.

## Testes automatizados

```bash
cd backend
mvn test
```

Os testes usam H2 isolado em memória e recriam o schema. A suíte demonstra inicialização do contexto, relacionamentos persistidos, cálculo de saldo, consultas, unicidade de e-mail, bloqueio de saldo insuficiente e criação/consulta de revisões Envers.

## Limites conscientes do escopo

A plataforma ainda não implementa autenticação e autorização por proprietário, transferência atômica entre carteiras, estorno contábil, categorias, lançamentos agendados ou integração com instituições financeiras. A mensageria possui idempotência por evento e comando, mas a API pública ainda precisaria de uma chave de idempotência própria. Em um sistema financeiro real, lançamentos não seriam apagados: seriam compensados por um novo lançamento. Esses pontos pertencem à evolução planejada, não ao MVP acadêmico atual.
