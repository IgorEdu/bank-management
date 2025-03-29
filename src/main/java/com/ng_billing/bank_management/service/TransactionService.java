package com.ng_billing.bank_management.service;

import com.ng_billing.bank_management.domain.Account;
import com.ng_billing.bank_management.domain.AccountDTO;
import com.ng_billing.bank_management.domain.Transaction;
import com.ng_billing.bank_management.domain.TransactionType;
import com.ng_billing.bank_management.exceptions.InsufficientBalanceException;
import com.ng_billing.bank_management.repository.TransactionRepository;
import org.springframework.stereotype.Service;

import javax.security.auth.login.AccountNotFoundException;
import java.math.BigDecimal;

@Service
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final AccountService accountService;

    public TransactionService(TransactionRepository transactionRepository, AccountService accountService) {
        this.transactionRepository = transactionRepository;
        this.accountService = accountService;
    }


//    public Transaction processTransaction(Transaction transaction) throws AccountNotFoundException {
//        Account account = accountService.getAccountByNumber(transaction.getAccount().getAccountNumber())
//                .orElseThrow(() -> new AccountNotFoundException("Conta não encontrada"));
//
//        if (account.getBalance() < transaction.getValue()) {
//            throw new InsufficientBalanceException("Saldo insuficiente para realizar a transação");
//        }
//
//        Transaction savedTransaction = transactionRepository.save(transaction);
//        accountService.decreaseBalance(account, transaction.getValue());
//
//        return savedTransaction;
//    }

    public Transaction processTransaction(Transaction transaction) throws AccountNotFoundException {
        Account account = accountService.getAccountByNumber(transaction.getAccount().getAccountNumber())
                .orElseThrow(() -> new AccountNotFoundException("Conta não encontrada"));

        BigDecimal transactionValue = transaction.getValue();
        BigDecimal totalAmount = calculateTotalAmount(transaction.getType(), transactionValue);

        if (totalAmount.compareTo(account.getBalance()) > 0) {
            throw new InsufficientBalanceException("Saldo insuficiente para realizar a transação.");
        }

        Transaction savedTransaction = transactionRepository.save(transaction);
        accountService.decreaseBalance(account, totalAmount);

        return savedTransaction;
    }

    private BigDecimal calculateTotalAmount(TransactionType type, BigDecimal value) {
        switch (type) {
            case D: // Débito
                return value.multiply(BigDecimal.valueOf(1.03));
            case C: // Crédito
                return value.multiply(BigDecimal.valueOf(1.05));
            case P: // Pix (sem taxa)
                return value;
            default:
                throw new IllegalArgumentException("Tipo de transação inválido.");
        }
    }
}
