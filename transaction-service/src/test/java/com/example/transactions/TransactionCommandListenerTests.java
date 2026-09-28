package com.example.transactions;

import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.UUID;

import org.junit.jupiter.api.Test;

import com.example.contracts.TransactionRequestedCommand;
import com.example.transactions.domain.TransactionType;
import com.example.transactions.dto.TransactionDtos.CreateTransaction;
import com.example.transactions.dto.TransactionDtos.TransactionView;
import com.example.transactions.messaging.TransactionCommandListener;
import com.example.transactions.messaging.TransactionEventPublisher;
import com.example.transactions.service.TransactionService;

class TransactionCommandListenerTests {
    @Test
    void processesWorkQueueCommandAndPublishesCompletedEvent() {
        TransactionService service = org.mockito.Mockito.mock(TransactionService.class);
        TransactionEventPublisher events = org.mockito.Mockito.mock(TransactionEventPublisher.class);
        UUID commandId = UUID.randomUUID();
        var command = new TransactionRequestedCommand(commandId, "transaction.create", 1,
                Instant.now(), commandId, "wallet-service", 1L, "CREDIT",
                new BigDecimal("25.00"), "Depósito");
        var input = new CreateTransaction(1L, TransactionType.CREDIT, new BigDecimal("25.00"), "Depósito");
        when(service.createFromCommand(commandId, input)).thenReturn(new TransactionView(9L, 1L, TransactionType.CREDIT,
                new BigDecimal("25.00"), "Depósito", LocalDateTime.now(), LocalDateTime.now()));

        new TransactionCommandListener(service, events).create(command);

        verify(events).publish(argThat(event -> event.commandId().equals(commandId)
                && event.transactionId().equals(9L) && event.status().equals("COMPLETED")));
    }
}
