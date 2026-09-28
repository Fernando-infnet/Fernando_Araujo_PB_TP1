package com.example.transactions.domain;

import java.time.Instant;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "processed_messages")
public class ProcessedMessage {
    @Id
    private UUID messageId;

    @Column(nullable = false, length = 80)
    private String messageType;

    @Column(nullable = false)
    private Instant processedAt;

    protected ProcessedMessage() {}

    public ProcessedMessage(UUID messageId, String messageType) {
        this.messageId = messageId;
        this.messageType = messageType;
        this.processedAt = Instant.now();
    }

    public UUID getMessageId() { return messageId; }
    public String getMessageType() { return messageType; }
    public Instant getProcessedAt() { return processedAt; }
}
