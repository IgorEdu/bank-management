package com.ng_billing.bank_management.domain.strategy;
import java.math.BigDecimal;

public interface TransactionTypeStrategy {
    BigDecimal calculateTotalAmount(BigDecimal value);
}

