package com.example.monolith.messaging;

import static com.example.contracts.MessagingTopology.WALLET_CREATED_KEY;
import static com.example.contracts.MessagingTopology.WALLET_EVENTS_EXCHANGE;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.example.contracts.WalletCreatedEvent;
import com.example.monolith.repository.WalletEventOutboxRepository;

@Component
@ConditionalOnProperty(name = "app.messaging.outbox.enabled", havingValue = "true", matchIfMissing = true)
public class WalletEventOutboxPublisher {
    private static final Logger log = LoggerFactory.getLogger(WalletEventOutboxPublisher.class);

    private final WalletEventOutboxRepository outbox;
    private final RabbitTemplate rabbit;

    public WalletEventOutboxPublisher(WalletEventOutboxRepository outbox, RabbitTemplate rabbit) {
        this.outbox = outbox;
        this.rabbit = rabbit;
    }

    @Scheduled(fixedDelayString = "${app.messaging.outbox.interval-ms:1000}")
    @Transactional
    public void publishPending() {
        for (var pending : outbox.findTop50ByPublishedAtIsNullOrderByOccurredAtAsc()) {
            try {
                var event = WalletCreatedEvent.create(pending.getEventId(), pending.getOccurredAt(),
                        pending.getWalletId(), pending.getUserId(), pending.getCurrency());
                rabbit.convertAndSend(WALLET_EVENTS_EXCHANGE, WALLET_CREATED_KEY, event,
                        message -> {
                            message.getMessageProperties().setMessageId(event.eventId().toString());
                            message.getMessageProperties().setCorrelationId(event.correlationId().toString());
                            return message;
                        });
                pending.markPublished();
            } catch (RuntimeException error) {
                pending.markAttempt();
                log.warn("Falha ao publicar evento {} (tentativa {})", pending.getEventId(),
                        pending.getAttempts(), error);
            }
        }
    }
}
