# Catálogo de eventos — TP4

## Envelope padrão

```json
{
  "eventId": "UUID",
  "eventType": "wallet.created",
  "eventVersion": 1,
  "occurredAt": "2026-09-27T18:00:00Z",
  "correlationId": "UUID",
  "source": "wallet-service",
  "payload": {}
}
```

## Catálogo

| Mensagem | Produtor | Consumidor | Exchange | Routing key | Fila |
|---|---|---|---|---|---|
| `wallet.created.v1` | Wallet Service | Transaction Service | `wallet.events` | `wallet.created.v1` | `transaction.wallet-events` |
| `transaction.create.v1` | Wallet Service | Transaction Service | `transaction.commands` | `transaction.create.v1` | `transaction.create` |
| `transaction.created.v1` | Transaction Service | Wallet Service | `transaction.events` | `transaction.created.v1` | `wallet.transaction-results` |
| `transaction.rejected.v1` | Transaction Service | Wallet Service | `transaction.events` | `transaction.rejected.v1` | `wallet.transaction-results` |

## Padrões de mensagens

- **Publicação/assinatura:** `wallet.events` e `transaction.events` permitem acrescentar consumidores sem alterar produtores.
- **Fila de trabalho:** os comandos de criação são distribuídos pela fila `transaction.create`; réplicas competem pelas mensagens.
- **Roteamento:** eventos usam topic routing e comandos usam uma routing key exata em direct exchange.
- **Retry e DLQ:** são realizadas três tentativas, iniciadas com intervalo de 500 ms; falhas finais seguem para `wallet.dead-letter`.

## Compatibilidade

Cada contrato carrega `eventVersion` ou `commandVersion`. Campos opcionais podem ser acrescentados mantendo a versão; remoções, mudanças de significado ou tipo exigem uma nova versão e routing key.
