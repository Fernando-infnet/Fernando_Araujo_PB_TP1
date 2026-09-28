# Vantagens e desvantagens — TP4

## Avaliação da arquitetura orientada a eventos

| Vantagens | Impacto no projeto |
|---|---|
| Menor acoplamento | Um serviço não precisa conhecer diretamente a implementação do outro |
| Escalabilidade independente | Consumidores podem receber mais réplicas conforme a fila cresce |
| Tolerância a falhas temporárias | Mensagens permanecem na fila enquanto o consumidor está indisponível |
| Extensibilidade | Novos consumidores podem ser adicionados sem alterar o produtor |

| Desvantagens | Mitigação adotada |
|---|---|
| Consistência eventual | Estados de processamento e comunicação clara no frontend |
| Possíveis mensagens duplicadas | Consumidores idempotentes por `eventId` |
| Depuração distribuída | `correlationId`, logs estruturados e tracing |
| Maior complexidade operacional | Documentação, métricas, retry e DLQ |

## Quando utilizar

<!-- Relacionar os cenários adequados e inadequados com o domínio da carteira digital. -->

## Conclusão

<!-- Justificar por que a solução adotada atende ao TP4. -->
