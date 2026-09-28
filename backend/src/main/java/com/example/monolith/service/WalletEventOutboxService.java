package com.example.monolith.service;

import org.springframework.stereotype.Service;

import com.example.monolith.domain.Wallet;
import com.example.monolith.domain.WalletEventOutbox;
import com.example.monolith.repository.WalletEventOutboxRepository;

@Service
public class WalletEventOutboxService {
    private final WalletEventOutboxRepository outbox;

    public WalletEventOutboxService(WalletEventOutboxRepository outbox) {
        this.outbox = outbox;
    }

    public void recordWalletCreated(Wallet wallet) {
        outbox.save(new WalletEventOutbox(
                wallet.getId(), wallet.getUser().getId(), wallet.getCurrency()));
    }
}
