package com.ng_billing.bank_management.strategy;

import java.math.BigDecimal;

public class CreditTransactionStrategy implements TransactionTypeStrategy {
    @Override
    public BigDecimal calculateTotalAmount(BigDecimal value) {
        return value.multiply(BigDecimal.valueOf(1.05));
    }
}
