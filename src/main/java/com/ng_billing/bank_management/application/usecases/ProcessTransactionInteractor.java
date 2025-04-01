package com.ng_billing.bank_management.application.usecases;

import com.ng_billing.bank_management.application.gateways.TransactionGateway;
import com.ng_billing.bank_management.domain.entity.Transaction;
import com.ng_billing.bank_management.domain.strategy.TransactionStrategyFactory;
import com.ng_billing.bank_management.domain.strategy.TransactionTypeStrategy;
import com.ng_billing.bank_management.infra.exceptions.AccountNotFoundException;
import com.ng_billing.bank_management.infra.exceptions.InsufficientBalanceException;
import com.ng_billing.bank_management.infra.persistence.AccountEntity;
import com.ng_billing.bank_management.infra.persistence.AccountRepository;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Optional;

@Component
public class ProcessTransactionInteractor {
    private final TransactionGateway transactionGateway;
    private final TransactionStrategyFactory strategyFactory;
    private final AccountRepository accountRepository;

    public ProcessTransactionInteractor(TransactionGateway transactionGateway, TransactionStrategyFactory strategyFactory, AccountRepository accountRepository) {
        this.transactionGateway = transactionGateway;
        this.strategyFactory = strategyFactory;
        this.accountRepository = accountRepository;
    }

//    public Transaction createTransaction(Transaction transaction) {
//        TransactionTypeStrategy strategy = strategyFactory.getStrategy(transaction.type());
//
//        BigDecimal totalAmount = strategy.calculateTotalAmount(transaction.amount());
//
//        if(totalAmount.compareTo(transaction.accountEntity().getBalance()) > 0){
//            throw new InsufficientBalanceException();
//        }
//
//        Transaction transactionWithTotalAmount = new Transaction(transaction.accountEntity(), transaction.type(), totalAmount);
//
//        return transactionGateway.createTransaction(transactionWithTotalAmount);
//    }

    public Transaction createTransaction(Transaction transaction) {
        AccountEntity account = accountRepository.findByAccountNumber(transaction.accountEntity().getAccountNumber());

        TransactionTypeStrategy strategy = strategyFactory.getStrategy(transaction.type());
        BigDecimal totalAmount = strategy.calculateTotalAmount(transaction.amount());

        if (totalAmount.compareTo(account.getBalance()) > 0) {
            throw new InsufficientBalanceException();
        }

        Transaction transactionWithTotalAmount = new Transaction(account, transaction.type(), totalAmount);
        return transactionGateway.createTransaction(transactionWithTotalAmount);
    }

}