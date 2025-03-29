package com.ng_billing.bank_management.strategy;

import java.math.BigDecimal;

public class PixTransactionStrategy implements TransactionTypeStrategy {
    @Override
    public BigDecimal calculateTotalAmount(BigDecimal value) {
        return value;
    }
}
