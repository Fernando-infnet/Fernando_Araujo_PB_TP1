# Roteiro de demonstração — TP4

## Preparação

Para executar tudo em containers, incluindo as aplicações:

```bash
docker compose up -d --build rabbitmq postgres transaction-postgres backend transaction-service frontend
```

Nesse modo, o painel do RabbitMQ fica em `http://localhost:15672` (`wallet`/`wallet`) e o frontend em `http://localhost:3000`.

Para desenvolvimento, suba somente a infraestrutura e execute as aplicações em terminais separados:

```bash
docker compose up -d rabbitmq postgres transaction-postgres
SPRING_PROFILES_ACTIVE=postgres mvn -pl backend -am spring-boot:run
SPRING_PROFILES_ACTIVE=postgres mvn -pl transaction-service -am spring-boot:run
npm run dev --prefix frontend
```

Nesse segundo modo, o frontend fica em `http://localhost:5173`. Os perfis PostgreSQL já usam, por padrão, as portas `5433` e `5434` publicadas pelo Compose.

## Roteiro

1. Apresentar a arquitetura anterior e seu acoplamento.
2. Explicar vantagens e desvantagens da arquitetura orientada a eventos.
3. Apresentar a arquitetura nova e os padrões de mensagens.
4. Criar uma carteira e mostrar `wallet.created.v1` sendo entregue à fila `transaction.wallet-events`.
5. Enviar `POST /api/transactions/async` e mostrar a resposta `202 PENDING`, o consumo do comando e o evento de resultado.
6. Desligar um consumidor e publicar uma nova mensagem.
7. Religar o consumidor e comprovar o processamento pendente.
8. Demonstrar retry e DLQ com uma mensagem inválida.
9. Reenviar um `eventId` e comprovar a idempotência.
10. Mostrar testes, commits e documentação.

## Comandos de apoio

```bash
# Ver filas, consumidores e quantidade de mensagens
docker compose exec rabbitmq rabbitmqctl list_queues name messages consumers

# Parar e religar apenas o consumidor para demonstrar resiliência
docker compose stop transaction-service
docker compose start transaction-service

# Executar toda a validação automatizada
mvn test
npm run build --prefix frontend

# Executar somente a prova automatizada de transporte, retry e DLQ
mvn -pl transaction-service -am test \
  -Dtest=RabbitMessagingIntegrationTests \
  -Dsurefire.failIfNoSpecifiedTests=false
```

Os comandos `stop` e `start` se aplicam ao modo totalmente containerizado. Se os serviços Java estiverem sendo executados pelo Maven, interrompa e reinicie o processo do `transaction-service` no terminal.

## Evidências

O vídeo deve mostrar a tela, o painel do RabbitMQ, os logs com identificadores das mensagens e o resultado dos testes. A apresentação audiovisual do projeto é centralizada no [README principal](../README.md), sem um vídeo separado para o TP4.
