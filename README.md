# Monólito Spring Boot com Front-End React — Escopo: Wallet / Carteira Digital

Este repositório implementa a primeira entrega (TP1) com escopo de uma aplicação de "Wallet / Carteira Digital".

Objetivos e requisitos do TP1 mantidos:
- Arquitetura em camadas: `controller`, `service`, `repository`, `domain`.
- Autoconfiguração do Spring Boot e Spring Data JPA.
- APIs REST para front-end consumir.
- Código limpo com foco em SOLID e DDD simples (domínios: usuário, carteira, transação).

## Estrutura
- `backend/`: Aplicação Spring Boot com entidades `User`, `Wallet`, `Transaction`.
- `frontend/`: Interface React que consome as APIs REST do backend.
- `docs/`: Planejamento e arquitetura (veja `fernando_araujo_TP1_planejamento.md`).

## Como executar

### Backend
1. Navegue para `backend`
2. Execute `mvn spring-boot:run` ou `java -jar target/monolith-0.0.1-SNAPSHOT.jar`
3. Endpoints principais:
	- `GET /api/transactions` — listar transações
	- `POST /api/transactions` — criar transação
	- `GET /h2-console` — console H2 (JDBC URL: `jdbc:h2:mem:tasksdb`)

### Frontend
1. Navegue para `frontend`
2. Execute `npm install`
3. Execute `npm run dev`
4. A interface estará em `http://localhost:5173` (ou porta alternativa escolhida pelo Vite)

## MVP implementado
- Registrar transações (entrada/saída)
- Listar transações

## Planejamento e documentação
Veja [fernando_araujo_TP1_planejamento.md](/fernando_araujo_TP1_planejamento.md) para o plano detalhado do TP1 (modelos, endpoints e próximos passos).
