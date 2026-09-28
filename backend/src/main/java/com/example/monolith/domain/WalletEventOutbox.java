package com.example.monolith.domain;

import java.time.Instant;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "wallet_event_outbox")
public class WalletEventOutbox {
    @Id
    private UUID eventId;

    @Column(nullable = false)
    private Long walletId;

    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false, length = 3)
    private String currency;

    @Column(nullable = false)
    private Instant occurredAt;

    private Instant publishedAt;

    @Column(nullable = false)
    private int attempts;

    protected WalletEventOutbox() {}

    public WalletEventOutbox(Long walletId, Long userId, String currency) {
        this.eventId = UUID.randomUUID();
        this.walletId = walletId;
        this.userId = userId;
        this.currency = currency;
        this.occurredAt = Instant.now();
    }

    public void markPublished() {
        this.publishedAt = Instant.now();
        this.attempts++;
    }

    public void markAttempt() { this.attempts++; }

    public UUID getEventId() { return eventId; }
    public Long getWalletId() { return walletId; }
    public Long getUserId() { return userId; }
    public String getCurrency() { return currency; }
    public Instant getOccurredAt() { return occurredAt; }
    public Instant getPublishedAt() { return publishedAt; }
    public int getAttempts() { return attempts; }
}
