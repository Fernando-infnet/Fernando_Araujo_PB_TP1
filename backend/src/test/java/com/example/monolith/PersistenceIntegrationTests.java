package com.example.monolith;

import static com.example.monolith.dto.ApiDtos.*;
import static org.assertj.core.api.Assertions.*;

import java.math.BigDecimal;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import com.example.monolith.domain.TransactionType;
import com.example.monolith.exception.BusinessException;
import com.example.monolith.repository.TransactionRepository;
import com.example.monolith.repository.UserRepository;
import com.example.monolith.repository.WalletRepository;
import com.example.monolith.service.HistoryService;
import com.example.monolith.service.PersistenceService;

@SpringBootTest
@ActiveProfiles("test")
class PersistenceIntegrationTests {
    @Autowired PersistenceService service;
    @Autowired HistoryService history;
    @Autowired TransactionRepository transactions;
    @Autowired WalletRepository wallets;
    @Autowired UserRepository users;

    @BeforeEach
    void clean() { transactions.deleteAll(); wallets.deleteAll(); users.deleteAll(); }

    @Test
    void persistsRelationshipsAndCalculatesBalanceInDatabase() {
        UserView user = service.createUser(new CreateUser("Ada", "ADA@example.com"));
        WalletView wallet = service.createWallet(new CreateWallet(user.id(), "brl"));
        service.createTransaction(new CreateTransaction(wallet.id(), TransactionType.CREDIT,
                new BigDecimal("100.00"), "Depósito"));
        service.createTransaction(new CreateTransaction(wallet.id(), TransactionType.DEBIT,
                new BigDecimal("35.25"), "Compra"));

        assertThat(service.balance(wallet.id()).balance()).isEqualByComparingTo("64.75");
        assertThat(service.listTransactions(wallet.id(), 10)).hasSize(2)
                .allMatch(item -> item.walletId().equals(wallet.id()));
    }

    @Test
    void preservesIntegrityWithUniqueEmailAndInsufficientBalanceRules() {
        UserView user = service.createUser(new CreateUser("Grace", "grace@example.com"));
        assertThatThrownBy(() -> service.createUser(new CreateUser("Outra", "GRACE@example.com")))
                .isInstanceOf(BusinessException.class);
        WalletView wallet = service.createWallet(new CreateWallet(user.id(), "USD"));
        assertThatThrownBy(() -> service.createTransaction(new CreateTransaction(wallet.id(),
                TransactionType.DEBIT, BigDecimal.ONE, null))).isInstanceOf(BusinessException.class)
                .hasMessageContaining("Saldo insuficiente");
    }

    @Test
    void recordsAndQueriesTransactionChangeHistory() {
        UserView user = service.createUser(new CreateUser("Linus", "linus@example.com"));
        WalletView wallet = service.createWallet(new CreateWallet(user.id(), "EUR"));
        TransactionView created = service.createTransaction(new CreateTransaction(wallet.id(),
                TransactionType.CREDIT, new BigDecimal("10.00"), "Inicial"));
        service.updateTransaction(created.id(), new UpdateTransaction("Descrição corrigida"));

        assertThat(history.transactionHistory(created.id())).hasSize(2);
        assertThat(history.transactionHistory(created.id()).get(0).operation()).isEqualTo("ADD");
        assertThat(history.transactionHistory(created.id()).get(1).operation()).isEqualTo("MOD");
    }
}
