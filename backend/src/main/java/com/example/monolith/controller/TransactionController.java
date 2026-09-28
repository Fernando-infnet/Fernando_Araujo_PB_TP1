package com.example.monolith.controller;

import static com.example.monolith.dto.ApiDtos.*;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import com.example.monolith.service.TransactionGatewayService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/transactions")
@CrossOrigin(origins = "http://localhost:5173")
public class TransactionController {
    private final TransactionGatewayService service;
    public TransactionController(TransactionGatewayService service) { this.service = service; }

    @PostMapping @ResponseStatus(HttpStatus.CREATED)
    public TransactionView create(@Valid @RequestBody CreateTransaction input) { return service.create(input); }
    @GetMapping("/{id}") public TransactionView get(@PathVariable Long id) { return service.get(id); }
    @PatchMapping("/{id}") public TransactionView update(@PathVariable Long id, @Valid @RequestBody UpdateTransaction input) { return service.update(id, input); }
    @DeleteMapping("/{id}") @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) { service.delete(id); }
    @GetMapping("/{id}/history") public List<HistoryView> history(@PathVariable Long id) { return service.history(id); }
}
