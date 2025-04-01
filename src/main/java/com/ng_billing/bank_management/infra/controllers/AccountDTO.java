package com.ng_billing.bank_management.infra.controllers;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record AccountDTO(
        @NotNull(message = "Número da conta não pode ser nulo")
        @JsonProperty("numero_conta")
        Integer accountNumber,

        @NotNull(message = "Saldo não pode ser nulo")
        @Positive(message = "Saldo deve ser positivo")
        @JsonProperty("saldo")
        BigDecimal balance
) {
}
