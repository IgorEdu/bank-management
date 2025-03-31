package com.ng_billing.bank_management.domain;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "transactions")
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "account_id", nullable = false)
    private Account account;

    @Enumerated(EnumType.STRING)
    private TransactionType type;
    @Column(name = "amount")
    private BigDecimal amount;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    public Transaction() {
    }

    public Transaction(UUID id, Account account, TransactionType type, BigDecimal amount) {
        this.id = id;
        this.account = account;
        this.type = type;
        this.amount = amount;
    }

    public Transaction(Account account, TransactionType type, BigDecimal amount) {
        this.account = account;
        this.type = type;
        this.amount = amount;
    }

    public Transaction(UUID id, Account account, TransactionType type, BigDecimal amount, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.account = account;
        this.type = type;
        this.amount = amount;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public UUID getId() {
        return id;
    }

    public Account getAccount() {
        return account;
    }

    public TransactionType getType() {
        return type;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
}
