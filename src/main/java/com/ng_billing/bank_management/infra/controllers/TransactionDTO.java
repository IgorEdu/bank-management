package com.ng_billing.bank_management.infra.controllers;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.ng_billing.bank_management.domain.entity.Transaction;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record TransactionDTO(
        @NotNull(message = "Forma de pagamento não pode ser nulo")
        @JsonProperty("forma_pagamento")
        Transaction.TransactionType type,

        @NotNull(message = "Número da conta não pode ser nulo")
        @JsonProperty("numero_conta")
        int accountNumber,

        @NotNull(message = "Valor não pode ser nulo")
        @Positive(message = "Valor deve ser positivo")
        @JsonProperty("valor")
        BigDecimal amount) {
}
