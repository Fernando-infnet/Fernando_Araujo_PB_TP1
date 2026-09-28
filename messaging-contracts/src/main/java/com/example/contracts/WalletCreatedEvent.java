package com.example.contracts;

import java.time.Instant;
import java.util.UUID;

public record WalletCreatedEvent(
        UUID eventId,
        String eventType,
        int eventVersion,
        Instant occurredAt,
        UUID correlationId,
        String source,
        Long walletId,
        Long userId,
        String currency) {

    public static WalletCreatedEvent create(UUID eventId, Instant occurredAt, Long walletId,
                                            Long userId, String currency) {
        return new WalletCreatedEvent(eventId, "wallet.created", 1, occurredAt, eventId,
                "wallet-service", walletId, userId, currency);
    }
}
