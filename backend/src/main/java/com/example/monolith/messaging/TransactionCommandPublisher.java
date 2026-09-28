package com.example.monolith.messaging;

import static com.example.contracts.MessagingTopology.TRANSACTION_COMMANDS_EXCHANGE;
import static com.example.contracts.MessagingTopology.TRANSACTION_CREATE_KEY;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import com.example.contracts.TransactionRequestedCommand;
import com.example.monolith.dto.ApiDtos.CreateTransaction;

@Component
public class TransactionCommandPublisher {
    private final RabbitTemplate rabbit;

    public TransactionCommandPublisher(RabbitTemplate rabbit) { this.rabbit = rabbit; }

    public TransactionRequestedCommand request(CreateTransaction input) {
        var command = TransactionRequestedCommand.create(input.walletId(), input.type().name(),
                input.amount(), input.description());
        rabbit.convertAndSend(TRANSACTION_COMMANDS_EXCHANGE, TRANSACTION_CREATE_KEY, command,
                message -> {
                    message.getMessageProperties().setMessageId(command.commandId().toString());
                    message.getMessageProperties().setCorrelationId(command.correlationId().toString());
                    return message;
                });
        return command;
    }
}
