# Implantação — TP5

## Pré-requisitos

- Docker Engine 24 ou superior com Docker Compose v2;
- cluster Kubernetes 1.27 ou superior e `kubectl`;
- Java 17 e Maven 3.9 para execução sem container;
- Node.js 22 e npm para desenvolvimento do frontend.

## Variáveis de ambiente

| Variável | Serviço | Obrigatória | Descrição |
|---|---|---|---|
| `DB_URL`, `DB_USERNAME`, `DB_PASSWORD` | Wallet Service | Sim em PostgreSQL | Conexão do banco de carteiras |
| `TRANSACTION_DB_URL`, `TRANSACTION_DB_USERNAME`, `TRANSACTION_DB_PASSWORD` | Transaction Service | Sim em PostgreSQL | Conexão do banco de transações |
| `RABBITMQ_HOST`, `RABBITMQ_USERNAME`, `RABBITMQ_PASSWORD` | Serviços Java | Sim | Conexão AMQP |
| `ZIPKIN_ENDPOINT` | Serviços Java | Não | Endpoint Zipkin do Tempo |
| `TRACING_SAMPLE_PROBABILITY` | Serviços Java | Não | Amostragem dos traces; padrão local `1.0` |
| `VITE_WALLET_API_URL`, `VITE_TRANSACTION_API_URL` | Build do frontend | Não | Rotas públicas das APIs |

Nunca registrar valores reais de senhas ou tokens neste documento.

## Docker Compose

```bash
docker compose config --quiet
docker compose build
docker compose up -d
docker compose ps
./scripts/smoke-test.sh
```

Aplicação: `http://localhost:3000`; RabbitMQ: `http://localhost:15672`; Grafana: `http://localhost:3001`; Prometheus: `http://localhost:9090`. Para encerrar sem apagar volumes, execute `docker compose down`.

Se houver conflito local, as portas publicadas podem ser alteradas sem modificar o arquivo, por exemplo: `FRONTEND_PORT=13000 BACKEND_PORT=18080 TRANSACTION_SERVICE_PORT=18081 WALLET_DB_PORT=15433 docker compose up -d`.

## Implantação no cluster

Substitua `OWNER` pelas imagens publicadas, troque as credenciais de demonstração no `kustomization.yaml` por Secrets gerenciados e execute:

```bash
kubectl kustomize k8s > /tmp/wallet-platform.yaml
kubectl apply -k k8s
kubectl rollout status deployment/backend -n wallet-platform
kubectl rollout status deployment/transaction-service -n wallet-platform
kubectl rollout status deployment/frontend -n wallet-platform
```

## Validação e rollback

Após configurar `wallet.local` para o IP do Ingress, rode `BASE_URL=http://wallet.local ./scripts/smoke-test.sh`. Se uma versão falhar, use `kubectl rollout undo deployment/<nome> -n wallet-platform` e confirme com `kubectl rollout status`.
