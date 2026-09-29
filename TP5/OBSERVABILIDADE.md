# Observabilidade — TP5

## Métricas

Os serviços expõem `/actuator/prometheus` por Micrometer. O Prometheus coleta os dois serviços e o plugin Prometheus do RabbitMQ a cada 15 segundos. O Grafana recebe os data sources Prometheus, Loki e Tempo e o dashboard **Wallet Platform — Operação** automaticamente por provisioning.

A exposição do endpoint Prometheus permanece habilitada junto com `health` e `info` em todos os perfis, inclusive `postgres`, usado pelos contêineres.

Métricas mínimas:

- disponibilidade, latência e erros HTTP;
- CPU e memória;
- conexões com banco;
- mensagens publicadas e consumidas;
- tamanho das filas, retries e DLQs.

## Dashboard operacional no Grafana

O dashboard provisionado **Wallet Platform — Operação** foi organizado para servir como tela principal da demonstração. Ele atualiza a cada 5 segundos e contém:

- cartões verdes/vermelhos separados para Wallet Service, Transaction Service e RabbitMQ;
- tráfego atual e requisições por serviço e status HTTP;
- taxa de erros 5xx e latência p95;
- tamanho das filas RabbitMQ;
- consumo de heap e CPU de cada JVM;
- logs ao vivo dos dois microsserviços, lado a lado.

Para visualizar:

```bash
docker compose up -d --build
```

Abra `http://localhost:3001`, entre com `admin` / `admin` e acesse **Dashboards > Wallet Platform > Wallet Platform — Operação**. Execute a aplicação em `http://localhost:3000` ou rode `./scripts/smoke-test.sh` para gerar tráfego. Os gráficos de taxa e p95 precisam de pelo menos duas coletas do Prometheus; aguarde aproximadamente 30 segundos após iniciar a pilha.

## Logs agregados

No perfil `postgres`, os serviços escrevem logs JSON em stdout com aplicação, nível, `traceId`, `spanId` e `correlationId`. Requisições aceitam `X-Correlation-ID` ou recebem um identificador novo, devolvido no mesmo header; consumidores AMQP colocam o identificador do contrato no contexto dos logs. O Promtail descobre containers pelo socket do Docker, interpreta o envelope Docker e o JSON da aplicação e envia os registros ao Loki com labels de serviço, container, aplicação e nível. No Explore do Grafana, uma consulta como `{service="backend"} |= "<correlationId>"` reúne os registros de uma operação.

O filtro HTTP registra método, rota, status e duração ao fim de cada chamada de API. O Promtail mantém somente os logs dos dois microsserviços, evitando ingerir registros antigos ou não relacionados de outros projetos Docker da máquina.

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
