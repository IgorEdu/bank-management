package com.ng_billing.bank_management.domain.strategy;

import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class CreditTransactionStrategy implements TransactionTypeStrategy {
    @Override
    public BigDecimal calculateTotalAmount(BigDecimal value) {
        return value.multiply(BigDecimal.valueOf(1.05));
    }
}
