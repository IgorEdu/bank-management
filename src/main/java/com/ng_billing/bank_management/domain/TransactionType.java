package com.ng_billing.bank_management.domain;

public enum TransactionType {
    P("Pix"),
    C("Cartão de Crédito"),
    D("Cartão de Débito");

    private final String description;

    TransactionType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
