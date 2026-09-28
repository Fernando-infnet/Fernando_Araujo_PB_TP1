# Manual de operações — TP5

## Verificação de saúde

```bash
docker compose ps
curl -f http://localhost:8080/actuator/health/readiness
curl -f http://localhost:8081/actuator/health/readiness
kubectl get pods,svc,hpa,pvc -n wallet-platform
kubectl describe pod <pod> -n wallet-platform
```

## Diagnóstico

Comece pelo estado e eventos do pod, consulte métricas no Grafana, encontre logs no Loki pelo serviço ou `traceId` e abra o mesmo identificador no Tempo. Para mensagens, consulte filas e consumers no RabbitMQ Management ou rode `rabbitmqctl list_queues name messages consumers` no pod/container.

## Incidentes

### Serviço indisponível

Verifique readiness/liveness, eventos, limites de recursos e conectividade com PostgreSQL/RabbitMQ. Se a falha começou após deploy, execute `kubectl rollout undo deployment/<serviço> -n wallet-platform`. Evite reiniciar bancos antes de verificar volumes e logs.

### Crescimento de fila ou DLQ

Confirme se há consumidores, examine a exceção original nos logs e corrija contrato, dados ou dependência. Reprocesse uma mensagem da DLQ somente depois da correção. Os consumidores são idempotentes por `eventId`/`commandId`, mas o reprocessamento ainda deve ser acompanhado.

### Falha de implantação

Use `kubectl rollout status` e `kubectl rollout history`. Reverta com `kubectl rollout undo`, confirme as probes e execute o smoke test. Preserve logs e a imagem defeituosa para diagnóstico.

## Backup e recuperação

PostgreSQL e RabbitMQ usam volumes nomeados no Compose e PVCs no Kubernetes. Um backup de produção deve usar `pg_dump` para cada banco e política de snapshot para os volumes. Valide a restauração em ambiente isolado e confirme usuários, carteiras, transações, Outbox e filas antes de liberar tráfego.
