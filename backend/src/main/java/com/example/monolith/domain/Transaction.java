package com.example.monolith.domain;

import java.math.BigDecimal;

import org.hibernate.envers.Audited;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Audited
@Table(name = "transactions", indexes = {
        @Index(name = "idx_transactions_wallet_created", columnList = "wallet_id,created_at"),
        @Index(name = "idx_transactions_wallet_type", columnList = "wallet_id,type")
})
public class Transaction extends BaseEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "wallet_id", nullable = false, updatable = false)
    private Wallet wallet;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10, updatable = false)
    private TransactionType type;

    @Column(nullable = false, precision = 19, scale = 2, updatable = false)
    private BigDecimal amount;

    @Column(length = 255)
    private String description;

    protected Transaction() {}
    public Transaction(Wallet wallet, TransactionType type, BigDecimal amount, String description) {
        this.wallet = wallet; this.type = type; this.amount = amount; this.description = description;
    }

    public Long getId() { return id; }
    public Wallet getWallet() { return wallet; }
    public TransactionType getType() { return type; }
    public BigDecimal getAmount() { return amount; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
}
