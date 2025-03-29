package com.ng_billing.bank_management.domain;

import jakarta.validation.constraints.NotNull;

public record AccountDTO(@NotNull int accountNumber,@NotNull float balance) {
}
