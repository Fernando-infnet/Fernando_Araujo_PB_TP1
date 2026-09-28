package com.example.monolith.service;

import static com.example.monolith.dto.ApiDtos.*;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.monolith.client.TransactionServiceClient;

@Service
public class TransactionGatewayService {
    private final TransactionServiceClient client;

    public TransactionGatewayService(TransactionServiceClient client) { this.client = client; }

    public TransactionView create(CreateTransaction input) { return client.create(input); }
    public TransactionView get(Long id) { return client.get(id); }
    public TransactionView update(Long id, UpdateTransaction input) { return client.update(id, input); }
    public void delete(Long id) { client.delete(id); }
    public List<HistoryView> history(Long id) { return client.history(id); }
    public List<TransactionView> list(Long walletId, int limit) {
        return client.list(walletId, Math.min(Math.max(limit, 1), 100));
    }
    public BalanceView balance(Long walletId) { return client.balance(walletId); }
}
