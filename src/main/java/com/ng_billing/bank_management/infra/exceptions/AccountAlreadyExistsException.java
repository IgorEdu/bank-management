package com.ng_billing.bank_management.infra.exceptions;

public class AccountAlreadyExistsException extends RuntimeException {
    public AccountAlreadyExistsException(int accountNumber) {
        super("Conta de número " + accountNumber + " já existente.");
    }
}

