package com.example.monolith.service;

import static com.example.monolith.dto.ApiDtos.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Locale;

import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.monolith.domain.Transaction;
import com.example.monolith.domain.TransactionType;
import com.example.monolith.domain.User;
import com.example.monolith.domain.Wallet;
import com.example.monolith.exception.BusinessException;
import com.example.monolith.exception.ResourceNotFoundException;
import com.example.monolith.repository.TransactionRepository;
import com.example.monolith.repository.UserRepository;
import com.example.monolith.repository.WalletRepository;

@Service
@Transactional
public class PersistenceService {
    private final UserRepository users;
    private final WalletRepository wallets;
    private final TransactionRepository transactions;

    public PersistenceService(UserRepository users, WalletRepository wallets, TransactionRepository transactions) {
        this.users = users; this.wallets = wallets; this.transactions = transactions;
    }

    public UserView createUser(CreateUser input) {
        String email = input.email().trim().toLowerCase(Locale.ROOT);
        if (users.existsByEmailIgnoreCase(email)) throw new BusinessException("E-mail já cadastrado");
        return userView(users.save(new User(input.name().trim(), email)));
    }

    @Transactional(readOnly = true)
    public List<UserView> listUsers() { return users.findAll().stream().map(this::userView).toList(); }

    @Transactional(readOnly = true)
    public UserView getUser(Long id) { return userView(requireUser(id)); }

    public UserView updateUser(Long id, UpdateUser input) {
        User user = requireUser(id);
        String email = input.email().trim().toLowerCase(Locale.ROOT);
        if (!user.getEmail().equalsIgnoreCase(email) && users.existsByEmailIgnoreCase(email))
            throw new BusinessException("E-mail já cadastrado");
        user.setName(input.name().trim()); user.setEmail(email);
        return userView(user);
    }

    public WalletView createWallet(CreateWallet input) {
        String currency = input.currency().toUpperCase(Locale.ROOT);
        return walletView(wallets.save(new Wallet(requireUser(input.userId()), currency)));
    }

    @Transactional(readOnly = true)
    public WalletView getWallet(Long id) { return walletView(requireWallet(id)); }

    @Transactional(readOnly = true)
    public List<WalletView> listWalletsByUser(Long userId) {
        requireUser(userId);
        return wallets.findByUserIdOrderByCreatedAtDesc(userId).stream().map(this::walletView).toList();
    }

    @Transactional(readOnly = true)
    public BalanceView balance(Long walletId) {
        Wallet wallet = requireWallet(walletId);
        return new BalanceView(walletId, wallet.getCurrency(), transactions.calculateBalance(walletId));
    }

    public TransactionView createTransaction(CreateTransaction input) {
        // Serializa lançamentos da mesma carteira para impedir dois débitos concorrentes
        // de validarem o mesmo saldo.
        Wallet wallet = wallets.findByIdForUpdate(input.walletId())
                .orElseThrow(() -> new ResourceNotFoundException("Carteira", input.walletId()));
        if (input.type() == TransactionType.DEBIT && transactions.calculateBalance(wallet.getId()).compareTo(input.amount()) < 0)
            throw new BusinessException("Saldo insuficiente");
        Transaction saved = transactions.save(new Transaction(wallet, input.type(), input.amount(), input.description()));
        return transactionView(saved);
    }

    @Transactional(readOnly = true)
    public TransactionView getTransaction(Long id) { return transactionView(requireTransaction(id)); }

    @Transactional(readOnly = true)
    public List<TransactionView> listTransactions(Long walletId, int limit) {
        requireWallet(walletId);
        int safeLimit = Math.min(Math.max(limit, 1), 100);
        return transactions.findByWalletIdOrderByCreatedAtDesc(walletId, PageRequest.of(0, safeLimit))
                .stream().map(this::transactionView).toList();
    }

    public TransactionView updateTransaction(Long id, UpdateTransaction input) {
        Transaction transaction = requireTransaction(id);
        transaction.setDescription(input.description());
        return transactionView(transaction);
    }

    public void deleteTransaction(Long id) { transactions.delete(requireTransaction(id)); }

    private User requireUser(Long id) { return users.findById(id).orElseThrow(() -> new ResourceNotFoundException("Usuário", id)); }
    private Wallet requireWallet(Long id) { return wallets.findById(id).orElseThrow(() -> new ResourceNotFoundException("Carteira", id)); }
    private Transaction requireTransaction(Long id) { return transactions.findById(id).orElseThrow(() -> new ResourceNotFoundException("Transação", id)); }
    private UserView userView(User u) { return new UserView(u.getId(), u.getName(), u.getEmail(), u.getCreatedAt(), u.getUpdatedAt()); }
    private WalletView walletView(Wallet w) { return new WalletView(w.getId(), w.getUser().getId(), w.getCurrency(), w.getCreatedAt(), w.getUpdatedAt()); }
    public TransactionView transactionView(Transaction t) { return new TransactionView(t.getId(), t.getWallet().getId(), t.getType(), t.getAmount(), t.getDescription(), t.getCreatedAt(), t.getUpdatedAt()); }
}
