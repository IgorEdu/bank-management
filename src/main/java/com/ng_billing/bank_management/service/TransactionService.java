package com.ng_billing.bank_management.service;

import com.ng_billing.bank_management.domain.Transaction;
import com.ng_billing.bank_management.repository.TransactionRepository;
import com.ng_billing.bank_management.usecase.ProcessTransactionUseCase;
import org.springframework.stereotype.Service;

import javax.security.auth.login.AccountNotFoundException;

@Service
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final ProcessTransactionUseCase processTransactionUseCase;

    public TransactionService(TransactionRepository transactionRepository, ProcessTransactionUseCase processTransactionUseCase) {
        this.transactionRepository = transactionRepository;
        this.processTransactionUseCase = processTransactionUseCase;
    }

    public Transaction processTransaction(Transaction transaction) throws AccountNotFoundException {
        processTransactionUseCase.execute(transaction);
        return transactionRepository.save(transaction);
    }
}
