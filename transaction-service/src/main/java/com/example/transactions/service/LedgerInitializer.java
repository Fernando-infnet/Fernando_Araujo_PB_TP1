package com.example.transactions.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.example.transactions.domain.WalletLedger;
import com.example.transactions.dto.TransactionDtos.WalletView;
import com.example.transactions.repository.WalletLedgerRepository;

@Service
public class LedgerInitializer {
    private final WalletLedgerRepository ledgers;

    public LedgerInitializer(WalletLedgerRepository ledgers) { this.ledgers = ledgers; }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void ensureExists(WalletView wallet) {
        if (!ledgers.existsById(wallet.id())) {
            ledgers.saveAndFlush(new WalletLedger(wallet.id(), wallet.currency()));
        }
    }
}
