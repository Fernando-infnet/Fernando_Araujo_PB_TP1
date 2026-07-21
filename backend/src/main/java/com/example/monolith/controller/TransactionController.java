package com.example.monolith.controller;

import static com.example.monolith.dto.ApiDtos.*;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import com.example.monolith.service.HistoryService;
import com.example.monolith.service.PersistenceService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/transactions")
@CrossOrigin(origins = "http://localhost:5173")
public class TransactionController {
    private final PersistenceService service;
    private final HistoryService history;
    public TransactionController(PersistenceService service, HistoryService history) { this.service = service; this.history = history; }

    @PostMapping @ResponseStatus(HttpStatus.CREATED)
    public TransactionView create(@Valid @RequestBody CreateTransaction input) { return service.createTransaction(input); }
    @GetMapping("/{id}") public TransactionView get(@PathVariable Long id) { return service.getTransaction(id); }
    @PatchMapping("/{id}") public TransactionView update(@PathVariable Long id, @Valid @RequestBody UpdateTransaction input) { return service.updateTransaction(id, input); }
    @DeleteMapping("/{id}") @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) { service.deleteTransaction(id); }
    @GetMapping("/{id}/history") public List<HistoryView> history(@PathVariable Long id) { return history.transactionHistory(id); }
}
