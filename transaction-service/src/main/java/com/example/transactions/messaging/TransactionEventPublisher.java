package com.example.transactions.messaging;

import static com.example.contracts.MessagingTopology.TRANSACTION_CREATED_KEY;
import static com.example.contracts.MessagingTopology.TRANSACTION_EVENTS_EXCHANGE;
import static com.example.contracts.MessagingTopology.TRANSACTION_REJECTED_KEY;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import com.example.contracts.TransactionResultEvent;

@Component
public class TransactionEventPublisher {
    private final RabbitTemplate rabbit;

    public TransactionEventPublisher(RabbitTemplate rabbit) { this.rabbit = rabbit; }

    public void publish(TransactionResultEvent event) {
        String routingKey = "COMPLETED".equals(event.status())
                ? TRANSACTION_CREATED_KEY : TRANSACTION_REJECTED_KEY;
        rabbit.convertAndSend(TRANSACTION_EVENTS_EXCHANGE, routingKey, event,
                message -> {
                    message.getMessageProperties().setMessageId(event.eventId().toString());
                    message.getMessageProperties().setCorrelationId(event.correlationId().toString());
                    return message;
                });
    }
}
