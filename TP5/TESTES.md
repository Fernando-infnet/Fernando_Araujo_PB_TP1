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
| Smoke | Saúde após deploy | `scripts/smoke-test.sh` | Automatizável após deploy |

## Como executar

```bash
mvn verify
npm test --prefix frontend
npm run build --prefix frontend
docker compose config --quiet
kubectl kustomize k8s > /tmp/wallet-platform.yaml
docker compose up -d --build
./scripts/smoke-test.sh
```

## Resultados

Em 28/09/2026, a suíte Maven foi aprovada localmente com Java 21 em compatibilidade Java 17: 18 testes ao todo, incluindo RabbitMQ real por Testcontainers e testes do header de correlação nos dois serviços. Os 3 testes do frontend e o build Vite também foram aprovados. O workflow repete a validação em Java 17 e Node 22 e publica os relatórios JaCoCo como artefato.

Testes que exigem um cluster real — recuperação de pod, HPA e rollback — fazem parte do roteiro de demonstração, pois dependem do ambiente disponibilizado para a apresentação.
