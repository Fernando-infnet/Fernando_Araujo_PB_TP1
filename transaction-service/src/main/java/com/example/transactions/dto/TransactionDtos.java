package com.example.transactions.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.example.transactions.domain.TransactionType;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public final class TransactionDtos {
    private TransactionDtos() {}

    public record CreateTransaction(@NotNull Long walletId,
                                    @NotNull TransactionType type,
                                    @NotNull @DecimalMin("0.01") BigDecimal amount,
                                    @Size(max = 255) String description) {}

    public record UpdateTransaction(@Size(max = 255) String description) {}

    public record TransactionView(Long id, Long walletId, TransactionType type, BigDecimal amount,
                                  String description, LocalDateTime createdAt, LocalDateTime updatedAt) {}

    public record BalanceView(Long walletId, String currency, BigDecimal balance) {}

    public record WalletView(Long id, Long userId, String currency,
                             LocalDateTime createdAt, LocalDateTime updatedAt) {}

    public record HistoryView(Number revision, String operation, LocalDateTime revisionAt, Object data) {}
}
