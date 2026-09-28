# Observabilidade — TP5

## Métricas

Os serviços expõem `/actuator/prometheus` por Micrometer. O Prometheus coleta os dois serviços e o plugin Prometheus do RabbitMQ a cada 15 segundos. O Grafana recebe os data sources Prometheus, Loki e Tempo e o dashboard **Wallet Platform — Operação** automaticamente por provisioning.

Métricas mínimas:

- disponibilidade, latência e erros HTTP;
- CPU e memória;
- conexões com banco;
- mensagens publicadas e consumidas;
- tamanho das filas, retries e DLQs.

## Logs agregados

No perfil `postgres`, os serviços escrevem logs JSON em stdout com aplicação, nível, `traceId`, `spanId` e `correlationId`. Requisições aceitam `X-Correlation-ID` ou recebem um identificador novo, devolvido no mesmo header; consumidores AMQP colocam o identificador do contrato no contexto dos logs. O Promtail descobre containers pelo socket do Docker, interpreta o envelope Docker e o JSON da aplicação e envia os registros ao Loki com labels de serviço, container, aplicação e nível. No Explore do Grafana, uma consulta como `{service="backend"} |= "<correlationId>"` reúne os registros de uma operação.

## Rastreamento distribuído

Micrometer Tracing com Brave instrumenta requisições HTTP e observações do Spring AMQP. Os spans são enviados em formato Zipkin ao Tempo; o Grafana consulta o Tempo e permite relacionar trace e logs. Em produção, reduza `TRACING_SAMPLE_PROBABILITY` conforme o volume.

## Alertas

| Alerta | Condição | Severidade | Ação |
|---|---|---|---|
| Serviço indisponível | `up == 0` por 2 minutos | Crítica | Consultar pods, logs e dependências |
| Erros HTTP | taxa 5xx acima de 5% por 5 minutos | Alta | Localizar trace e verificar rollback |
| DLQ crescendo | mensagens em `wallet.dead-letter` acima de zero | Alta | Corrigir causa antes de reprocessar |
| Fila acumulada | crescimento contínuo por 10 minutos | Média | Verificar consumidor e avaliar escala |

Os três primeiros alertas estão versionados em `observability/alerts.yml` e carregados pelo Prometheus. Nesta entrega acadêmica eles aparecem na tela **Alerts** do Prometheus; o roteamento de notificações por e-mail ou chat exige um Alertmanager e credenciais do ambiente.

## Acesso

- Grafana: `http://localhost:3001`
- Prometheus: `http://localhost:9090`
- Tempo API: `http://localhost:3200`
- Loki API: `http://localhost:3100`
- RabbitMQ Management: `http://localhost:15672`

As credenciais no Compose são exclusivamente locais e devem ser substituídas no ambiente de entrega.
