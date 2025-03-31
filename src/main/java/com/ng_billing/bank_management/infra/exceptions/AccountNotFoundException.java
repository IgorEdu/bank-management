package com.ng_billing.bank_management.infra.exceptions;

public class AccountNotFoundException extends RuntimeException {
    public AccountNotFoundException(int accountNumber) {
        super("Conta não encontrada para o número: " + accountNumber);
    }
}
