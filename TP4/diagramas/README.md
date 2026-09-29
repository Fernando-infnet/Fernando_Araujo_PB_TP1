# Diagramas do TP4

Os diagramas estão em Mermaid para permanecerem versionáveis e renderizarem diretamente no GitHub. A arquitetura atual completa também aparece em [ARQUITETURA.md](../ARQUITETURA.md).

## Imagem pronta para a apresentação final

Abra esta imagem em tela cheia durante o vídeo:

[![Arquitetura final da Wallet Platform — TP3 ao TP5](../../TP5/diagramas/arquitetura-final.png)](../../TP5/diagramas/arquitetura-final.svg)

- [abrir SVG em alta qualidade](../../TP5/diagramas/arquitetura-final.svg);
- [abrir PNG](../../TP5/diagramas/arquitetura-final.png).

O diagrama consolida a evolução do TP3 ao TP5: frontend, Wallet Service,
Transaction Service, bancos separados, RabbitMQ, Outbox, DLQ, Docker/Kubernetes
e a observabilidade com Prometheus, Promtail, Loki, Tempo e Grafana.

## Arquitetura anterior (TP3)

```mermaid
flowchart LR
    UI[Frontend] -->|REST| WS[Wallet Service]
    UI -->|REST| TS[Transaction Service]
    WS --> WDB[(Wallet DB)]
    TS --> TDB[(Transaction DB)]
    TS -->|Feign: valida carteira| WS
```

O Transaction Service dependia de uma resposta síncrona do Wallet Service para processar operações relacionadas a uma carteira.

## Sequência principal orientada a eventos

```mermaid
sequenceDiagram
    actor U as Usuário
    participant W as Wallet Service
    participant WO as Outbox
    participant R as RabbitMQ
    participant T as Transaction Service
    participant TD as Transaction DB

    U->>W: POST /api/wallets
    W->>WO: Salva carteira + wallet.created.v1
    W-->>U: 201 Created
    WO->>R: Publica wallet.created.v1
    R->>T: Entrega pela transaction.wallet-events
    T->>TD: Cria projeção e registra eventId

    U->>W: POST /api/transactions/async
    W->>R: Publica transaction.create.v1
    W-->>U: 202 PENDING + commandId
    R->>T: Entrega pela transaction.create
    T->>TD: Cria transação usando commandId
    T->>R: Publica transaction.created.v1
    R->>W: Entrega resultado
```

## Falha, retry e DLQ

```mermaid
sequenceDiagram
    participant R as RabbitMQ
    participant C as Listener Spring
    participant D as wallet.dead-letter

    R->>C: Entrega mensagem
    C-->>C: Processamento falha
    R->>C: Retry 2
    C-->>C: Processamento falha
    R->>C: Retry 3
    C-->>R: Falha final / reject sem requeue
    R->>D: Dead-letter via wallet.dlx
    Note over D: Operador inspeciona e corrige a causa antes do reprocessamento
```

## Consumidor indisponível

```mermaid
sequenceDiagram
    participant P as Produtor
    participant R as RabbitMQ
    participant C as Consumidor

    C--xR: Consumidor indisponível
    P->>R: Publica evento em fila durável
    Note over R: Mensagem permanece pendente
    C->>R: Consumidor retorna
    R->>C: Entrega mensagem acumulada
    C-->>R: ACK após processamento
```
