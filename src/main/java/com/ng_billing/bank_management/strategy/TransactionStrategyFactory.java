package com.ng_billing.bank_management.strategy;

import com.ng_billing.bank_management.domain.TransactionType;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class TransactionStrategyFactory {

    private final Map<TransactionType, TransactionTypeStrategy> strategies;

    public TransactionStrategyFactory(
            DebitTransactionStrategy debitStrategy,
            CreditTransactionStrategy creditStrategy,
            PixTransactionStrategy pixStrategy
    ) {
        this.strategies = Map.of(
                TransactionType.D, debitStrategy,
                TransactionType.C, creditStrategy,
                TransactionType.P, pixStrategy
        );
    }

    public TransactionTypeStrategy getStrategy(TransactionType type) {
        return strategies.getOrDefault(type,
                transaction -> { throw new IllegalArgumentException("Tipo de transação inválido."); });
    }
}
