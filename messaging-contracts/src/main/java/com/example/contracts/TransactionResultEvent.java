package com.example.contracts;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record TransactionResultEvent(
        UUID eventId,
        String eventType,
        int eventVersion,
        Instant occurredAt,
        UUID correlationId,
        String source,
        UUID commandId,
        Long transactionId,
        Long walletId,
        String transactionType,
        BigDecimal amount,
        String status,
        String reason) {

    public static TransactionResultEvent created(UUID commandId, Long transactionId, Long walletId,
                                                 String transactionType, BigDecimal amount) {
        return new TransactionResultEvent(UUID.randomUUID(), "transaction.created", 1, Instant.now(),
                commandId, "transaction-service", commandId, transactionId, walletId,
                transactionType, amount, "COMPLETED", null);
    }

    public static TransactionResultEvent rejected(UUID commandId, Long walletId,
                                                  String transactionType, BigDecimal amount, String reason) {
        return new TransactionResultEvent(UUID.randomUUID(), "transaction.rejected", 1, Instant.now(),
                commandId, "transaction-service", commandId, null, walletId,
                transactionType, amount, "REJECTED", reason);
    }
}
