# Plano e resultados de testes — TP5

## Estratégia

| Categoria | Escopo | Ferramenta | Situação |
|---|---|---|---|
| Unitário | Regras de negócio e cliente HTTP | JUnit/Mockito e Node Test Runner | Automatizado |
| Integração | Persistência e RabbitMQ real | Spring Boot Test e Testcontainers | Automatizado |
| Contrato | Serialização e consumo de mensagens | Jackson, AMQP e módulo compartilhado | Automatizado |
| Build | Artefatos Java e frontend | Maven e Vite | Automatizado no CI |
| Configuração | Compose e renderização Kubernetes | Docker Compose e Kustomize | Automatizado no CI |
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

Em 27/09/2026, a suíte Maven foi aprovada localmente com Java 21 em compatibilidade Java 17: 8 testes no Transaction Service, incluindo RabbitMQ real por Testcontainers, além dos testes do Wallet Service. O build Vite também foi aprovado. O workflow repete a validação em Java 17 e Node 22 e publica os relatórios JaCoCo como artefato.

Testes que exigem um cluster real — recuperação de pod, HPA e rollback — fazem parte do roteiro de demonstração, pois dependem do ambiente disponibilizado para a apresentação.
