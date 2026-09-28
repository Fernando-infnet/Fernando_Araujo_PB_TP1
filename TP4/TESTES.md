# Plano e resultados de testes — TP4

## Escopo

- testes unitários das regras de negócio;
- testes de integração com RabbitMQ e banco;
- testes dos contratos de eventos;
- teste de idempotência;
- teste de retry e DLQ;
- teste do fluxo completo entre os serviços.

## Casos de teste

| Identificador | Cenário | Resultado esperado | Resultado obtido |
|---|---|---|---|
| TP4-01 | Publicar criação de carteira | Consumidor atualiza sua projeção local | Pendente |
| TP4-02 | Reenviar o mesmo `eventId` | Evento não produz efeito duplicado | Pendente |
| TP4-03 | Desligar o consumidor | Mensagem permanece disponível | Pendente |
| TP4-04 | Processar mensagem inválida | Mensagem chega à DLQ | Pendente |

## Como executar

<!-- Adicionar os comandos reais depois que a suíte estiver implementada. -->

## Evidências

<!-- Registrar data, ambiente, quantidade de testes, cobertura e links para relatórios. -->
