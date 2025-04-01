package com.ng_billing.bank_management.domain.strategy;

import com.ng_billing.bank_management.domain.entity.Transaction;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class TransactionStrategyFactory {

    private final Map<Transaction.TransactionType, TransactionTypeStrategy> strategies;

    public TransactionStrategyFactory(
            DebitTransactionStrategy debitStrategy,
            CreditTransactionStrategy creditStrategy,
            PixTransactionStrategy pixStrategy
    ) {
        this.strategies = Map.of(
                Transaction.TransactionType.D, debitStrategy,
                Transaction.TransactionType.C, creditStrategy,
                Transaction.TransactionType.P, pixStrategy
        );
    }

    public TransactionTypeStrategy getStrategy(Transaction.TransactionType type) {
        return strategies.getOrDefault(type,
                transaction -> { throw new IllegalArgumentException("Tipo de transação inválido."); });
    }
}
