package com.example.transactions.service;

import static com.example.transactions.dto.TransactionDtos.*;

import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.transactions.domain.Transaction;
import com.example.transactions.domain.WalletLedger;
import com.example.transactions.exception.BusinessException;
import com.example.transactions.exception.ResourceNotFoundException;
import com.example.transactions.repository.TransactionRepository;
import com.example.transactions.repository.WalletLedgerRepository;

@Service
@Transactional
public class TransactionService {
    private final TransactionRepository transactions;
    private final WalletLedgerRepository ledgers;

    public TransactionService(TransactionRepository transactions, WalletLedgerRepository ledgers) {
        this.transactions = transactions;
        this.ledgers = ledgers;
    }

    public TransactionView create(CreateTransaction input) {
        return create(input, null);
    }

    public TransactionView createFromCommand(UUID commandId, CreateTransaction input) {
        return transactions.findByExternalReference(commandId)
                .map(this::view)
                .orElseGet(() -> create(input, commandId));
    }

    private TransactionView create(CreateTransaction input, UUID externalReference) {
        WalletLedger ledger = requireLedgerForUpdate(input.walletId());
        try {
            ledger.apply(input.type(), input.amount());
        } catch (IllegalStateException error) {
            throw new BusinessException(error.getMessage());
        }
        return view(transactions.save(new Transaction(
                input.walletId(), input.type(), input.amount(), normalizeDescription(input.description()),
                externalReference)));
    }

    @Transactional(readOnly = true)
    public TransactionView get(Long id) { return view(requireTransaction(id)); }

    @Transactional(readOnly = true)
    public List<TransactionView> list(Long walletId, int limit) {
        requireLedger(walletId);
        int safeLimit = Math.min(Math.max(limit, 1), 100);
        return transactions.findByWalletIdOrderByCreatedAtDesc(walletId, PageRequest.of(0, safeLimit))
                .stream().map(this::view).toList();
    }

    @Transactional(readOnly = true)
    public BalanceView balance(Long walletId) {
        WalletLedger ledger = requireLedger(walletId);
        return new BalanceView(walletId, ledger.getCurrency(), ledger.getBalance());
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

    private WalletLedger requireLedgerForUpdate(Long walletId) {
        return ledgers.findByWalletIdForUpdate(walletId)
                .orElseThrow(() -> new ResourceNotFoundException("Carteira sincronizada", walletId));
    }

    private WalletLedger requireLedger(Long walletId) {
        return ledgers.findById(walletId)
                .orElseThrow(() -> new ResourceNotFoundException("Carteira sincronizada", walletId));
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
