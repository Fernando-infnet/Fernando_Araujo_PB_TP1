package com.example.transactions.domain;

import java.math.BigDecimal;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Version;

@Entity
@Table(name = "wallet_ledgers")
public class WalletLedger {
    @Id
    @Column(name = "wallet_id")
    private Long walletId;

    @Column(nullable = false, length = 3)
    private String currency;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal balance;

    @Version
    private long version;

    protected WalletLedger() {}

    public WalletLedger(Long walletId, String currency) {
        this.walletId = walletId;
        this.currency = currency;
        this.balance = BigDecimal.ZERO.setScale(2);
    }

    public void apply(TransactionType type, BigDecimal amount) {
        if (type == TransactionType.DEBIT && balance.compareTo(amount) < 0) {
            throw new IllegalStateException("Saldo insuficiente");
        }
        balance = type == TransactionType.CREDIT ? balance.add(amount) : balance.subtract(amount);
    }

    public void reverse(TransactionType type, BigDecimal amount) {
        if (type == TransactionType.CREDIT && balance.compareTo(amount) < 0) {
            throw new IllegalStateException("Exclusão deixaria o saldo negativo");
        }
        balance = type == TransactionType.CREDIT ? balance.subtract(amount) : balance.add(amount);
    }

    public Long getWalletId() { return walletId; }
    public String getCurrency() { return currency; }
    public BigDecimal getBalance() { return balance; }
}
