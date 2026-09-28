package com.example.monolith.client;

import static com.example.monolith.dto.ApiDtos.*;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.monolith.config.FeignErrorConfiguration;

@FeignClient(name = "transaction-service", configuration = FeignErrorConfiguration.class)
public interface TransactionServiceClient {
    @PostMapping("/api/transactions")
    TransactionView create(@RequestBody CreateTransaction input);

    @GetMapping("/api/transactions/{id}")
    TransactionView get(@PathVariable("id") Long id);

    @PatchMapping("/api/transactions/{id}")
    TransactionView update(@PathVariable("id") Long id, @RequestBody UpdateTransaction input);

    @DeleteMapping("/api/transactions/{id}")
    void delete(@PathVariable("id") Long id);

    @GetMapping("/api/transactions/{id}/history")
    List<HistoryView> history(@PathVariable("id") Long id);

    @GetMapping("/api/transactions/wallet/{walletId}")
    List<TransactionView> list(@PathVariable("walletId") Long walletId, @RequestParam("limit") int limit);

    @GetMapping("/api/transactions/wallet/{walletId}/balance")
    BalanceView balance(@PathVariable("walletId") Long walletId);
}
