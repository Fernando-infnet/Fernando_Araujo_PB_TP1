# Plano e resultados de testes — TP5

## Estratégia

| Categoria | Escopo | Ferramenta | Situação |
|---|---|---|---|
| Unitário | Regras de negócio e cliente HTTP | JUnit/Mockito e Node Test Runner | Automatizado |
| Integração | Persistência e RabbitMQ real | Spring Boot Test e Testcontainers | Automatizado |
| Contrato | Serialização e consumo de mensagens | Jackson, AMQP e módulo compartilhado | Automatizado |
| Build | Artefatos Java e frontend | Maven e Vite | Automatizado no CI |
| Configuração | Compose e manifests Kubernetes | Docker Compose, Kustomize e Kubeconform | Automatizado no CI |
| Segurança | Vulnerabilidades das imagens | Trivy | Automatizado no CI |
| Resiliência | Reentrega, idempotência e reposição de pod | JUnit/RabbitMQ e roteiro manual | Parcialmente automatizado |
| Smoke | Saúde e fluxo integrado usuário → carteira → RabbitMQ → transação → saldo | `scripts/smoke-test.sh` | Automatizável após deploy |

## Como executar

```bash
mvn verify
npm test --prefix frontend
npm run build --prefix frontend
docker compose config --quiet
kubectl kustomize k8s > /tmp/wallet-platform.yaml
docker compose up -d --build
./scripts/smoke-test.sh
curl --fail http://localhost:8080/actuator/prometheus
curl --fail http://localhost:8081/actuator/prometheus
```

## Resultados

Em 28/09/2026, a suíte Maven foi aprovada localmente com Java 21 em compatibilidade Java 17: 18 testes ao todo, incluindo RabbitMQ real por Testcontainers e testes do header de correlação nos dois serviços. Os 9 testes do frontend e o build Vite também foram aprovados; eles cobrem o cliente HTTP, os filtros do extrato, a geração do CSV e os cálculos de período, totais e série do gráfico. O smoke test cria dados descartáveis e valida a integração HTTP, PostgreSQL e RabbitMQ entre os serviços. O workflow repete a validação em Java 17 e Node 22 e publica os relatórios JaCoCo como artefato.

Na mesma validação, o Kubeconform aprovou os 24 recursos Kubernetes sem erros; todos os contêineres com healthcheck ficaram `healthy`; os três targets do Prometheus ficaram `UP`; Grafana, Loki e Tempo responderam; e o Tempo recebeu traces dos dois serviços. O mesmo `correlationId` do smoke test foi localizado no Loki nos logs do backend e do serviço de transações.

Testes que exigem um cluster real — recuperação de pod, HPA e rollback — fazem parte do roteiro de demonstração, pois dependem do ambiente disponibilizado para a apresentação.
