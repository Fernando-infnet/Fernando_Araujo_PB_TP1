package com.example.transactions;

import static com.example.transactions.dto.TransactionDtos.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import com.example.contracts.WalletCreatedEvent;
import com.example.transactions.domain.TransactionType;
import com.example.transactions.domain.WalletLedger;
import com.example.transactions.exception.BusinessException;
import com.example.transactions.exception.ResourceNotFoundException;
import com.example.transactions.repository.TransactionRepository;
import com.example.transactions.repository.WalletLedgerRepository;
import com.example.transactions.repository.ProcessedMessageRepository;
import com.example.transactions.messaging.WalletEventListener;
import com.example.transactions.service.HistoryService;
import com.example.transactions.service.TransactionService;

@SpringBootTest
@ActiveProfiles("test")
class TransactionServiceIntegrationTests {
    @Autowired TransactionService service;
    @Autowired HistoryService history;
    @Autowired TransactionRepository transactions;
    @Autowired WalletLedgerRepository ledgers;
    @Autowired ProcessedMessageRepository processed;
    @Autowired WalletEventListener walletEvents;

    @BeforeEach
    void clean() {
        transactions.deleteAll();
        ledgers.deleteAll();
        processed.deleteAll();
        ledgers.save(new WalletLedger(1L, "BRL"));
    }

    @Test
    void persistsTransactionsAndMaintainsDedicatedLedger() {
        service.create(new CreateTransaction(1L, TransactionType.CREDIT, new BigDecimal("100.00"), "Depósito"));
        service.create(new CreateTransaction(1L, TransactionType.DEBIT, new BigDecimal("35.25"), "Compra"));

        assertThat(service.balance(1L).balance()).isEqualByComparingTo("64.75");
        assertThat(service.list(1L, 10)).hasSize(2).allMatch(item -> item.walletId().equals(1L));
    }

    @Test
    void rejectsDebitWithoutFunds() {
        assertThatThrownBy(() -> service.create(new CreateTransaction(
                1L, TransactionType.DEBIT, BigDecimal.ONE, null)))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("Saldo insuficiente");
    }

    @Test
    void auditsDescriptionChangesAndReversesDeletedTransactions() {
        TransactionView created = service.create(new CreateTransaction(
                1L, TransactionType.CREDIT, new BigDecimal("10.00"), "Inicial"));
        service.update(created.id(), new UpdateTransaction("Descrição corrigida"));

        assertThat(history.transactionHistory(created.id())).extracting(HistoryView::operation)
                .containsExactly("ADD", "MOD");
        service.delete(created.id());
        assertThat(service.balance(1L).balance()).isEqualByComparingTo("0.00");
    }

    @Test
    void rejectsWalletThatWasNotSynchronizedByEvent() {
        assertThatThrownBy(() -> service.balance(99L)).isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void consumesWalletEventIdempotently() {
        UUID eventId = UUID.randomUUID();
        WalletCreatedEvent event = WalletCreatedEvent.create(eventId, Instant.now(), 2L, 10L, "USD");

        walletEvents.onWalletCreated(event);
        walletEvents.onWalletCreated(event);

        assertThat(ledgers.findById(2L)).hasValueSatisfying(ledger ->
                assertThat(ledger.getCurrency()).isEqualTo("USD"));
        assertThat(processed.count()).isEqualTo(1);
    }

    @Test
    void processesTheSameCommandOnlyOnce() {
        UUID commandId = UUID.randomUUID();
        CreateTransaction input = new CreateTransaction(
                1L, TransactionType.CREDIT, new BigDecimal("15.00"), "Comando");

        TransactionView first = service.createFromCommand(commandId, input);
        TransactionView duplicate = service.createFromCommand(commandId, input);

        assertThat(duplicate.id()).isEqualTo(first.id());
        assertThat(transactions.count()).isEqualTo(1);
        assertThat(service.balance(1L).balance()).isEqualByComparingTo("15.00");
    }
}
