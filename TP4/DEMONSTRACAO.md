# Roteiro de demonstração — TP4

## Preparação

Suba a infraestrutura e execute as aplicações em terminais separados:

```bash
docker compose up -d wallet-db transaction-db rabbitmq
mvn -pl backend -am spring-boot:run
mvn -pl transaction-service -am spring-boot:run
npm run dev --prefix frontend
```

O painel do RabbitMQ fica em `http://localhost:15672` (`wallet`/`wallet`). O frontend fica em `http://localhost:5173`.

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
docker exec wallet-rabbitmq rabbitmqctl list_queues name messages consumers

# Parar e religar apenas o consumidor para demonstrar resiliência
docker stop wallet-transaction-service
docker start wallet-transaction-service

# Executar toda a validação automatizada
mvn test
npm run build --prefix frontend
```

Se os serviços Java forem executados diretamente pelo Maven, interrompa e reinicie o processo do `transaction-service` no lugar dos dois comandos `docker` acima.

## Evidências

O vídeo deve mostrar a tela, o painel do RabbitMQ, os logs com identificadores das mensagens e o resultado dos testes. Depois da gravação, publique o arquivo em um local acessível ao avaliador e substitua o marcador em [README.md](README.md).
