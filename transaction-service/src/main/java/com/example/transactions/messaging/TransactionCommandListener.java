package com.example.transactions.messaging;

import static com.example.contracts.MessagingTopology.TRANSACTION_CREATE_QUEUE;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import com.example.contracts.TransactionRequestedCommand;
import com.example.contracts.TransactionResultEvent;
import com.example.transactions.domain.TransactionType;
import com.example.transactions.dto.TransactionDtos.CreateTransaction;
import com.example.transactions.exception.BusinessException;
import com.example.transactions.exception.ResourceNotFoundException;
import com.example.transactions.service.TransactionService;

@Component
public class TransactionCommandListener {
    private static final Logger log = LoggerFactory.getLogger(TransactionCommandListener.class);
    private final TransactionService transactions;
    private final TransactionEventPublisher events;

    public TransactionCommandListener(TransactionService transactions, TransactionEventPublisher events) {
        this.transactions = transactions;
        this.events = events;
    }

    @RabbitListener(queues = TRANSACTION_CREATE_QUEUE)
    public void create(TransactionRequestedCommand command) {
        try (MDC.MDCCloseable ignored = MDC.putCloseable("correlationId", command.correlationId().toString())) {
            try {
                var input = new CreateTransaction(command.walletId(),
                        TransactionType.valueOf(command.transactionType()), command.amount(), command.description());
                var created = transactions.createFromCommand(command.commandId(), input);
                events.publish(TransactionResultEvent.created(command.commandId(), created.id(),
                        created.walletId(), created.type().name(), created.amount()));
                log.info("Comando processado commandId={} transactionId={}", command.commandId(), created.id());
            } catch (BusinessException | ResourceNotFoundException | IllegalArgumentException error) {
                events.publish(TransactionResultEvent.rejected(command.commandId(), command.walletId(),
                        command.transactionType(), command.amount(), error.getMessage()));
                log.warn("Comando rejeitado commandId={} reason={}", command.commandId(), error.getMessage());
            }
        }
    }
}
