package com.ng_billing.bank_management.infra.exceptions;

public class InsufficientBalanceException extends RuntimeException {
    public InsufficientBalanceException() {
        super("Saldo insuficiente para realizar a transação.");
    }
}

