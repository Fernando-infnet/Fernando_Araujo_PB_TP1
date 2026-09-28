package com.example.transactions.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.example.transactions.dto.TransactionDtos.WalletView;

@FeignClient(name = "wallet-service")
public interface WalletClient {
    @GetMapping("/api/wallets/{id}")
    WalletView getWallet(@PathVariable("id") Long id);
}
