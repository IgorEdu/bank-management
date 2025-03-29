package com.ng_billing.bank_management.service;

import com.ng_billing.bank_management.domain.Account;
import com.ng_billing.bank_management.domain.AccountDTO;
import com.ng_billing.bank_management.domain.Transaction;
import com.ng_billing.bank_management.exceptions.InsufficientBalanceException;
import com.ng_billing.bank_management.repository.TransactionRepository;
import org.springframework.stereotype.Service;

import javax.security.auth.login.AccountNotFoundException;

@Service
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final AccountService accountService;

    public TransactionService(TransactionRepository transactionRepository, AccountService accountService) {
        this.transactionRepository = transactionRepository;
        this.accountService = accountService;
    }


    public Transaction processTransaction(Transaction transaction) throws AccountNotFoundException {
        Account account = accountService.getAccountByNumber(transaction.getAccount().getAccountNumber())
                .orElseThrow(() -> new AccountNotFoundException("Conta não encontrada"));

        if (account.getBalance() < transaction.getValue()) {
            throw new InsufficientBalanceException("Saldo insuficiente para realizar a transação");
        }

        Transaction savedTransaction = transactionRepository.save(transaction);
        accountService.decreaseBalance(account, transaction.getValue());

        return savedTransaction;
    }
}
