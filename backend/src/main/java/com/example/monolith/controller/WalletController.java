package com.example.monolith.controller;

import static com.example.monolith.dto.ApiDtos.*;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import com.example.monolith.service.PersistenceService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/wallets")
@CrossOrigin(origins = "http://localhost:5173")
public class WalletController {
    private final PersistenceService service;
    public WalletController(PersistenceService service) { this.service = service; }

    @PostMapping @ResponseStatus(HttpStatus.CREATED)
    public WalletView create(@Valid @RequestBody CreateWallet input) { return service.createWallet(input); }
    @GetMapping("/{id}") public WalletView get(@PathVariable Long id) { return service.getWallet(id); }
    @GetMapping("/user/{userId}") public List<WalletView> byUser(@PathVariable Long userId) { return service.listWalletsByUser(userId); }
    @GetMapping("/{id}/balance") public BalanceView balance(@PathVariable Long id) { return service.balance(id); }
    @GetMapping("/{id}/transactions")
    public List<TransactionView> transactions(@PathVariable Long id, @RequestParam(defaultValue = "50") int limit) {
        return service.listTransactions(id, limit);
    }
}
