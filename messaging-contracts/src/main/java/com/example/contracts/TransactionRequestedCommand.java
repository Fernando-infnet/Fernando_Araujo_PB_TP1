package com.example.contracts;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record TransactionRequestedCommand(
        UUID commandId,
        String commandType,
        int commandVersion,
        Instant occurredAt,
        UUID correlationId,
        String source,
        Long walletId,
        String transactionType,
        BigDecimal amount,
        String description) {

    public static TransactionRequestedCommand create(Long walletId, String transactionType,
                                                     BigDecimal amount, String description) {
        UUID id = UUID.randomUUID();
        return new TransactionRequestedCommand(id, "transaction.create", 1, Instant.now(), id,
                "wallet-service", walletId, transactionType, amount, description);
    }
}
