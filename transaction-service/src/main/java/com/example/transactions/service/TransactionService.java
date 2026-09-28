package com.example.transactions.service;

import static com.example.transactions.dto.TransactionDtos.*;

import java.util.List;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.transactions.client.WalletClient;
import com.example.transactions.domain.Transaction;
import com.example.transactions.domain.WalletLedger;
import com.example.transactions.exception.BusinessException;
import com.example.transactions.exception.ResourceNotFoundException;
import com.example.transactions.exception.WalletServiceUnavailableException;
import com.example.transactions.repository.TransactionRepository;
import com.example.transactions.repository.WalletLedgerRepository;

import feign.FeignException;

@Service
@Transactional
public class TransactionService {
    private final TransactionRepository transactions;
    private final WalletLedgerRepository ledgers;
    private final WalletClient wallets;
    private final LedgerInitializer ledgerInitializer;

    public TransactionService(TransactionRepository transactions, WalletLedgerRepository ledgers,
                              WalletClient wallets, LedgerInitializer ledgerInitializer) {
        this.transactions = transactions;
        this.ledgers = ledgers;
        this.wallets = wallets;
        this.ledgerInitializer = ledgerInitializer;
    }

    public TransactionView create(CreateTransaction input) {
        WalletView wallet = requireWallet(input.walletId());
        WalletLedger ledger = lockOrCreateLedger(wallet);
        try {
            ledger.apply(input.type(), input.amount());
        } catch (IllegalStateException error) {
            throw new BusinessException(error.getMessage());
        }
        return view(transactions.save(new Transaction(
                input.walletId(), input.type(), input.amount(), normalizeDescription(input.description()))));
    }

    @Transactional(readOnly = true)
    public TransactionView get(Long id) { return view(requireTransaction(id)); }

    @Transactional(readOnly = true)
    public List<TransactionView> list(Long walletId, int limit) {
        requireWallet(walletId);
        int safeLimit = Math.min(Math.max(limit, 1), 100);
        return transactions.findByWalletIdOrderByCreatedAtDesc(walletId, PageRequest.of(0, safeLimit))
                .stream().map(this::view).toList();
    }

    @Transactional(readOnly = true)
    public BalanceView balance(Long walletId) {
        WalletView wallet = requireWallet(walletId);
        return ledgers.findById(walletId)
                .map(ledger -> new BalanceView(walletId, ledger.getCurrency(), ledger.getBalance()))
                .orElseGet(() -> new BalanceView(walletId, wallet.currency(), java.math.BigDecimal.ZERO.setScale(2)));
    }

    public TransactionView update(Long id, UpdateTransaction input) {
        Transaction transaction = requireTransaction(id);
        transaction.setDescription(normalizeDescription(input.description()));
        return view(transaction);
    }

    public void delete(Long id) {
        Transaction transaction = requireTransaction(id);
        WalletLedger ledger = ledgers.findByWalletIdForUpdate(transaction.getWalletId())
                .orElseThrow(() -> new IllegalStateException("Saldo da carteira não inicializado"));
        try {
            ledger.reverse(transaction.getType(), transaction.getAmount());
        } catch (IllegalStateException error) {
            throw new BusinessException(error.getMessage());
        }
        transactions.delete(transaction);
    }

    private WalletLedger lockOrCreateLedger(WalletView wallet) {
        if (ledgers.findByWalletIdForUpdate(wallet.id()).isEmpty()) {
            try {
                ledgerInitializer.ensureExists(wallet);
            } catch (DataIntegrityViolationException ignoredConcurrentCreation) {
                // Another instance initialized the same ledger first.
            }
        }
        return ledgers.findByWalletIdForUpdate(wallet.id())
                .orElseThrow(() -> new IllegalStateException("Não foi possível inicializar o saldo da carteira"));
    }

    private WalletView requireWallet(Long walletId) {
        try {
            return wallets.getWallet(walletId);
        } catch (FeignException.NotFound error) {
            throw new ResourceNotFoundException("Carteira", walletId);
        } catch (FeignException error) {
            throw new WalletServiceUnavailableException();
        }
    }

    private Transaction requireTransaction(Long id) {
        return transactions.findById(id).orElseThrow(() -> new ResourceNotFoundException("Transação", id));
    }

    private String normalizeDescription(String description) {
        if (description == null || description.isBlank()) return null;
        return description.trim();
    }

    TransactionView view(Transaction transaction) {
        return new TransactionView(transaction.getId(), transaction.getWalletId(), transaction.getType(),
                transaction.getAmount(), transaction.getDescription(), transaction.getCreatedAt(), transaction.getUpdatedAt());
    }
}
