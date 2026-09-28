package com.example.transactions.controller;

import static com.example.transactions.dto.TransactionDtos.*;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.example.transactions.service.HistoryService;
import com.example.transactions.service.TransactionService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/transactions")
@CrossOrigin(origins = "${app.cors.allowed-origin:http://localhost:5173}")
public class TransactionController {
    private final TransactionService transactions;
    private final HistoryService history;

    public TransactionController(TransactionService transactions, HistoryService history) {
        this.transactions = transactions;
        this.history = history;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TransactionView create(@Valid @RequestBody CreateTransaction input) { return transactions.create(input); }

    @GetMapping("/{id}")
    public TransactionView get(@PathVariable Long id) { return transactions.get(id); }

    @PatchMapping("/{id}")
    public TransactionView update(@PathVariable Long id, @Valid @RequestBody UpdateTransaction input) {
        return transactions.update(id, input);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) { transactions.delete(id); }

    @GetMapping("/{id}/history")
    public List<HistoryView> history(@PathVariable Long id) { return history.transactionHistory(id); }

    @GetMapping("/wallet/{walletId}")
    public List<TransactionView> list(@PathVariable Long walletId,
                                      @RequestParam(defaultValue = "50") int limit) {
        return transactions.list(walletId, limit);
    }

    @GetMapping("/wallet/{walletId}/balance")
    public BalanceView balance(@PathVariable Long walletId) { return transactions.balance(walletId); }
}
