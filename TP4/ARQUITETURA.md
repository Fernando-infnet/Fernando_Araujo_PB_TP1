# Arquitetura orientada a eventos — TP4

## Objetivo

Documentar a transformação da comunicação síncrona entre os serviços para uma arquitetura orientada a eventos com RabbitMQ.

## Arquitetura anterior

<!-- Descrever os serviços, bancos e chamadas REST existentes antes do TP4. -->

## Arquitetura proposta

<!-- Inserir o diagrama disponível em diagramas/ e explicar produtores, consumidores, exchanges e filas. -->

## Responsabilidades

| Componente | Responsabilidade |
|---|---|
| Wallet Service | Gerenciar usuários e carteiras |
| Transaction Service | Gerenciar lançamentos, histórico e saldo |
| RabbitMQ | Transportar comandos e eventos entre os serviços |
| Frontend | Consumir as APIs REST e apresentar o estado das operações |

## Consistência e resiliência

<!-- Explicar consistência eventual, idempotência, transactional outbox, retry e DLQ. -->

## Decisões arquiteturais

<!-- Registrar as decisões tomadas, alternativas descartadas e justificativas. -->
