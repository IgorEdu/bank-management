package com.ng_billing.bank_management.domain;

import jakarta.persistence.*;

import java.util.UUID;

@Entity
@Table(name = "accounts")
public class Account {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    private int account_number;
    private float balance;

    public Account() {
    }

    public Account(UUID id, int account_number, float balance) {
        this.id = id;
        this.account_number = account_number;
        this.balance = balance;
    }

    public Account(int account_number, float balance) {
        this.account_number = account_number;
        this.balance = balance;
    }

    public UUID getId() {
        return id;
    }

    public int getAccount_number() {
        return account_number;
    }

    public float getBalance() {
        return balance;
    }
}
