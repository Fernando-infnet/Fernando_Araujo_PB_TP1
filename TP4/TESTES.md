# Plano e resultados de testes — TP4

## Escopo

- testes unitários das regras de negócio;
- testes de integração com RabbitMQ e banco;
- testes dos contratos de eventos;
- teste de idempotência;
- teste de retry e DLQ;
- teste do fluxo completo entre os serviços.

## Casos de teste

| Identificador | Cenário | Resultado esperado | Resultado obtido |
|---|---|---|---|
| TP4-01 | Publicar criação de carteira | Consumidor atualiza sua projeção local | Aprovado com RabbitMQ/Testcontainers |
| TP4-02 | Reenviar o mesmo `eventId` | Evento não produz efeito duplicado | Aprovado |
| TP4-03 | Reenviar o mesmo `commandId` | Transação e saldo não são duplicados | Aprovado |
| TP4-04 | Persistir carteira | Evento fica registrado no Outbox | Aprovado |
| TP4-05 | Processar comando assíncrono | Evento de conclusão é publicado | Aprovado |

## Como executar

```bash
mvn test
npm run build --prefix frontend
docker compose config --quiet
```

O teste `RabbitMessagingIntegrationTests` inicia um RabbitMQ real com Testcontainers. Em ambientes sem Docker ele é marcado como ignorado; os demais testes não dependem de infraestrutura externa.

## Evidências

Validação local realizada com Java 21 executando código compilado para Java 17, Maven e Docker 29. O teste de transporte real publicou `wallet.created.v1`, consumiu a mensagem e confirmou a criação da projeção no banco.
