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

A arquitetura orientada a eventos é adequada quando uma ação precisa disparar trabalho em mais de um serviço, quando os consumidores devem escalar de forma independente ou quando uma indisponibilidade temporária não pode interromper o produtor. Neste projeto, a criação de carteira é um bom exemplo: o Wallet Service confirma a gravação local e o Transaction Service atualiza sua projeção assim que puder consumir o evento.

Ela não deve substituir toda comunicação síncrona. Consultas em que o usuário precisa de uma resposta imediata e consistente continuam expostas por REST. Também não é a melhor escolha para operações simples dentro do mesmo limite transacional, pois acrescentaria broker, consistência eventual e mecanismos de idempotência sem benefício proporcional.

## Custos aceitos no projeto

- A projeção de carteiras do Transaction Service pode ficar momentaneamente atrasada.
- Operadores precisam acompanhar filas, mensagens rejeitadas e o Outbox.
- Cada consumidor precisa aceitar reentregas sem duplicar efeitos.
- Contratos de mensagens precisam ser versionados e evoluir de forma compatível.

## Conclusão

A solução combina REST na borda com eventos e comandos entre os serviços. Essa divisão reduz a dependência direta que existia no TP3, permite recuperar mensagens após indisponibilidades e demonstra publicação/assinatura, fila de trabalho, roteamento, retry, DLQ e idempotência. O custo de consistência eventual é controlado pelo Outbox e pelas chaves únicas de processamento.
