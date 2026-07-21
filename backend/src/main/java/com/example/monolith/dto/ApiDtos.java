package com.example.monolith.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.example.monolith.domain.TransactionType;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public final class ApiDtos {
    private ApiDtos() {}

    public record CreateUser(@NotBlank @Size(max = 120) String name,
                             @NotBlank @Email @Size(max = 180) String email) {}
    public record UpdateUser(@NotBlank @Size(max = 120) String name,
                             @NotBlank @Email @Size(max = 180) String email) {}
    public record UserView(Long id, String name, String email, LocalDateTime createdAt, LocalDateTime updatedAt) {}

    public record CreateWallet(@NotNull Long userId, @NotBlank @Size(min = 3, max = 3) String currency) {}
    public record WalletView(Long id, Long userId, String currency, LocalDateTime createdAt, LocalDateTime updatedAt) {}
    public record BalanceView(Long walletId, String currency, BigDecimal balance) {}

    public record CreateTransaction(@NotNull Long walletId, @NotNull TransactionType type,
                                    @NotNull @DecimalMin(value = "0.01") BigDecimal amount,
                                    @Size(max = 255) String description) {}
    public record UpdateTransaction(@Size(max = 255) String description) {}
    public record TransactionView(Long id, Long walletId, TransactionType type, BigDecimal amount,
                                  String description, LocalDateTime createdAt, LocalDateTime updatedAt) {}
    public record HistoryView(Number revision, String operation, LocalDateTime revisionAt, Object data) {}
}
