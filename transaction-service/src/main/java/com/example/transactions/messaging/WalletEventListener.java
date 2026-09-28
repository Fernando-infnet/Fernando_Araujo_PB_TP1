package com.example.transactions.messaging;

import static com.example.contracts.MessagingTopology.WALLET_EVENTS_QUEUE;

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
    private final WalletLedgerRepository ledgers;
    private final ProcessedMessageRepository processed;

    public WalletEventListener(WalletLedgerRepository ledgers, ProcessedMessageRepository processed) {
        this.ledgers = ledgers;
        this.processed = processed;
    }

    @RabbitListener(queues = WALLET_EVENTS_QUEUE)
    @Transactional
    public void onWalletCreated(WalletCreatedEvent event) {
        if (processed.existsById(event.eventId())) return;
        if (!ledgers.existsById(event.walletId())) {
            ledgers.save(new WalletLedger(event.walletId(), event.currency()));
        }
        processed.save(new ProcessedMessage(event.eventId(), event.eventType()));
    }
}
