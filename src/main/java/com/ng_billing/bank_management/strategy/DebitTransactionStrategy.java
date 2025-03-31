package com.ng_billing.bank_management.strategy;

import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class DebitTransactionStrategy implements TransactionTypeStrategy {
    @Override
    public BigDecimal calculateTotalAmount(BigDecimal value) {
        return value.multiply(BigDecimal.valueOf(1.03));
    }
}