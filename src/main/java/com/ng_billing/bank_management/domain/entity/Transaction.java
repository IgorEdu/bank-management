package com.ng_billing.bank_management.domain.entity;

import com.ng_billing.bank_management.infra.persistence.AccountEntity;

import java.math.BigDecimal;

public record Transaction(
        AccountEntity accountEntity,
        TransactionType type,
        BigDecimal amount
) {
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
}
