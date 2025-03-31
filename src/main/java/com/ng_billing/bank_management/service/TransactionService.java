package com.ng_billing.bank_management.service;

import com.ng_billing.bank_management.domain.Account;
import com.ng_billing.bank_management.domain.Transaction;
import com.ng_billing.bank_management.domain.TransactionType;
import com.ng_billing.bank_management.infra.exceptions.InsufficientBalanceException;
import com.ng_billing.bank_management.repository.TransactionRepository;
import com.ng_billing.bank_management.strategy.CreditTransactionStrategy;
import com.ng_billing.bank_management.strategy.DebitTransactionStrategy;
import com.ng_billing.bank_management.strategy.PixTransactionStrategy;
import com.ng_billing.bank_management.strategy.TransactionTypeStrategy;
import org.springframework.stereotype.Service;

import javax.security.auth.login.AccountNotFoundException;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@Service
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final AccountService accountService;

    private final Map<TransactionType, TransactionTypeStrategy> transactionStrategies = new HashMap<>();

    public TransactionService(TransactionRepository transactionRepository, AccountService accountService) {
        this.transactionRepository = transactionRepository;
        this.accountService = accountService;

        transactionStrategies.put(TransactionType.D, new DebitTransactionStrategy());
        transactionStrategies.put(TransactionType.C, new CreditTransactionStrategy());
        transactionStrategies.put(TransactionType.P, new PixTransactionStrategy());
    }

    public Transaction processTransaction(Transaction transaction) throws AccountNotFoundException {
        Account account = accountService.getAccountByNumber(transaction.getAccount().getAccountNumber())
                .orElseThrow(() -> new AccountNotFoundException("Conta não encontrada"));

        BigDecimal transactionValue = transaction.getAmount();
        TransactionTypeStrategy strategy = transactionStrategies.get(transaction.getType());

        if (strategy == null) {
            throw new IllegalArgumentException("Tipo de transação inválido.");
        }

        BigDecimal totalAmount = strategy.calculateTotalAmount(transactionValue);

        if (totalAmount.compareTo(account.getBalance()) > 0) {
            throw new InsufficientBalanceException();
        }

        Transaction savedTransaction = transactionRepository.save(transaction);
        accountService.decreaseBalance(account, totalAmount);

        return savedTransaction;
    }
}