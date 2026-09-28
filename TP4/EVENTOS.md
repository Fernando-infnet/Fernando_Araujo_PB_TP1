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

| Evento | Produtor | Consumidor | Exchange | Routing key | Fila |
|---|---|---|---|---|---|
| `wallet.created.v1` | Wallet Service | Transaction Service | A definir | A definir | A definir |
| `wallet.updated.v1` | Wallet Service | Transaction Service | A definir | A definir | A definir |
| `transaction.created.v1` | Transaction Service | Wallet Service | A definir | A definir | A definir |
| `transaction.rejected.v1` | Transaction Service | Wallet Service | A definir | A definir | A definir |

## Padrões de mensagens

- **Publicação/assinatura:** documentar o caso de uso e os consumidores.
- **Fila de trabalho:** documentar o comando processado assincronamente.
- **Roteamento:** documentar exchanges e routing keys.
- **Retry e DLQ:** documentar quantidade de tentativas, intervalo e destino final.

## Compatibilidade

<!-- Explicar versionamento dos eventos e regras para alterações compatíveis. -->
