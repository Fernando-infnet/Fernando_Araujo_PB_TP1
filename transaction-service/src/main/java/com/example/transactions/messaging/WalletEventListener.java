package com.example.transactions.messaging;

import static com.example.contracts.MessagingTopology.WALLET_EVENTS_QUEUE;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.example.contracts.WalletCreatedEvent;
import com.example.transactions.domain.ProcessedMessage;
import com.example.transactions.domain.WalletLedger;
import com.example.transactions.repository.ProcessedMessageRepository;
import com.example.transactions.repository.WalletLedgerRepository;

@Component
public class WalletEventListener {
    private static final Logger log = LoggerFactory.getLogger(WalletEventListener.class);
    private final WalletLedgerRepository ledgers;
    private final ProcessedMessageRepository processed;

    public WalletEventListener(WalletLedgerRepository ledgers, ProcessedMessageRepository processed) {
        this.ledgers = ledgers;
        this.processed = processed;
    }

    @RabbitListener(queues = WALLET_EVENTS_QUEUE)
    @Transactional
    public void onWalletCreated(WalletCreatedEvent event) {
        try (MDC.MDCCloseable ignored = MDC.putCloseable("correlationId", event.correlationId().toString())) {
            if (processed.existsById(event.eventId())) {
                log.info("Evento duplicado ignorado eventId={}", event.eventId());
                return;
            }
            if (!ledgers.existsById(event.walletId())) {
                ledgers.save(new WalletLedger(event.walletId(), event.currency()));
            }
            processed.save(new ProcessedMessage(event.eventId(), event.eventType()));
            log.info("Projeção de carteira atualizada eventId={} walletId={}", event.eventId(), event.walletId());
        }
    }
}
