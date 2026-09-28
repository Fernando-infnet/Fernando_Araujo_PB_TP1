package com.example.transactions;

import static com.example.transactions.dto.TransactionDtos.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;

import com.example.transactions.client.WalletClient;
import com.example.transactions.domain.TransactionType;
import com.example.transactions.exception.BusinessException;
import com.example.transactions.exception.ResourceNotFoundException;
import com.example.transactions.repository.TransactionRepository;
import com.example.transactions.repository.WalletLedgerRepository;
import com.example.transactions.service.HistoryService;
import com.example.transactions.service.TransactionService;

import feign.FeignException;
import feign.Request;
import feign.RequestTemplate;

@SpringBootTest
@ActiveProfiles("test")
class TransactionServiceIntegrationTests {
    @Autowired TransactionService service;
    @Autowired HistoryService history;
    @Autowired TransactionRepository transactions;
    @Autowired WalletLedgerRepository ledgers;
    @MockBean WalletClient wallets;

    @BeforeEach
    void clean() {
        transactions.deleteAll();
        ledgers.deleteAll();
        when(wallets.getWallet(1L)).thenReturn(wallet(1L, "BRL"));
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
    void rejectsUnknownWalletReportedByWalletService() {
        Request request = Request.create(Request.HttpMethod.GET, "/api/wallets/99", java.util.Map.of(), null,
                new RequestTemplate());
        when(wallets.getWallet(99L)).thenThrow(new FeignException.NotFound(
                "not found", request, null, java.util.Map.of()));

        assertThatThrownBy(() -> service.balance(99L)).isInstanceOf(ResourceNotFoundException.class);
    }

    private WalletView wallet(Long id, String currency) {
        return new WalletView(id, 10L, currency, LocalDateTime.now(), LocalDateTime.now());
    }
}
