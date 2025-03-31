package com.ng_billing.bank_management.service;

import com.ng_billing.bank_management.domain.Account;
import com.ng_billing.bank_management.domain.Transaction;
import com.ng_billing.bank_management.domain.TransactionType;
import com.ng_billing.bank_management.infra.exceptions.AccountNotFoundException;
import com.ng_billing.bank_management.infra.exceptions.InsufficientBalanceException;
import com.ng_billing.bank_management.repository.TransactionRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;


import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TransactionServiceTest {

    @InjectMocks
    private TransactionService transactionService;

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private AccountService accountService;

    @Mock
    private Account account;

    private Transaction transaction;

    @Test
    @DisplayName("Deve realizar a transação de débito com sucesso quando a conta tiver saldo suficiente")
    void shouldCompleteDebitTransactionWhenAccountHasSufficientBalance() throws AccountNotFoundException, InsufficientBalanceException {
        BigDecimal transactionValue = BigDecimal.valueOf(100.00);
        BigDecimal fee = transactionValue.multiply(BigDecimal.valueOf(0.03));
        BigDecimal totalAmount = transactionValue.add(fee);

        Transaction transaction = new Transaction(account, TransactionType.D, transactionValue);

        when(account.getBalance()).thenReturn(BigDecimal.valueOf(103));
        when(accountService.getAccountByNumber(anyInt())).thenReturn(Optional.of(account));
        when(transactionRepository.save(any(Transaction.class))).thenReturn(transaction);

        Transaction result = transactionService.processTransaction(transaction);

        verify(transactionRepository).save(transaction);
        assertNotNull(result);
        assertEquals(transactionValue, result.getAmount());
        assertEquals(TransactionType.D, result.getType());

        verify(accountService).decreaseBalance(account, totalAmount);
    }

    @Test
    @DisplayName("Deve realizar a transação de crédito com sucesso quando a conta tiver saldo suficiente")
    void shouldCompleteCreditTransactionWhenAccountHasSufficientBalance() throws AccountNotFoundException, InsufficientBalanceException {
        BigDecimal transactionValue = BigDecimal.valueOf(100);
        BigDecimal fee = transactionValue.multiply(BigDecimal.valueOf(0.05));
        BigDecimal totalAmount = transactionValue.add(fee);

        Transaction transaction = new Transaction(account, TransactionType.C, transactionValue);

        when(account.getBalance()).thenReturn(BigDecimal.valueOf(105));
        when(accountService.getAccountByNumber(anyInt())).thenReturn(Optional.of(account));
        when(transactionRepository.save(any(Transaction.class))).thenReturn(transaction);

        Transaction result = transactionService.processTransaction(transaction);

        verify(transactionRepository).save(transaction);
        assertNotNull(result);
        assertEquals(transactionValue, result.getAmount());
        assertEquals(TransactionType.C, result.getType());

        verify(accountService).decreaseBalance(account, totalAmount);
    }

    @Test
    @DisplayName("Deve realizar a transação de Pix com sucesso quando a conta tiver saldo suficiente")
    void shouldCompletePixTransactionWhenAccountHasSufficientBalance() throws AccountNotFoundException, InsufficientBalanceException {
        BigDecimal transactionValue = BigDecimal.valueOf(100);

        Transaction transaction = new Transaction(account, TransactionType.P, transactionValue);

        when(account.getBalance()).thenReturn(BigDecimal.valueOf(100));
        when(accountService.getAccountByNumber(anyInt())).thenReturn(Optional.of(account));
        when(transactionRepository.save(any(Transaction.class))).thenReturn(transaction);

        Transaction result = transactionService.processTransaction(transaction);

        verify(transactionRepository).save(transaction);
        assertNotNull(result);
        assertEquals(transactionValue, result.getAmount());
        assertEquals(TransactionType.P, result.getType());

        verify(accountService).decreaseBalance(account, transactionValue);
    }

    @Test
    @DisplayName("Deve lançar exceção quando a conta não tiver saldo suficiente para transação tipo Pix")
    void shouldThrowExceptionWhenAccountHasInsufficientBalancePixTransaction() {
        BigDecimal transactionValue = BigDecimal.valueOf(100);

        Transaction transaction = new Transaction(account, TransactionType.P, transactionValue);

        when(accountService.getAccountByNumber(anyInt())).thenReturn(Optional.of(account));
        when(account.getBalance()).thenReturn(BigDecimal.valueOf(99.99));

        assertThrows(InsufficientBalanceException.class, () -> {
            transactionService.processTransaction(transaction);
        });
    }

    @Test
    @DisplayName("Deve lançar exceção quando a conta não tiver saldo suficiente para transferência tipo Débito")
    void shouldThrowExceptionWhenAccountHasInsufficientBalanceDebitTransaction() {
        BigDecimal transactionValue = BigDecimal.valueOf(100);

        Transaction transaction = new Transaction(account, TransactionType.D, transactionValue);

        when(accountService.getAccountByNumber(anyInt())).thenReturn(Optional.of(account));
        when(account.getBalance()).thenReturn(BigDecimal.valueOf(102.99));

        assertThrows(InsufficientBalanceException.class, () -> {
            transactionService.processTransaction(transaction);
        });
    }

    @Test
    @DisplayName("Deve lançar exceção quando a conta não tiver saldo suficiente para transferência tipo Crédito")
    void shouldThrowExceptionWhenAccountHasInsufficientBalanceCreditTransaction() {
        BigDecimal transactionValue = BigDecimal.valueOf(100);

        Transaction transaction = new Transaction(account, TransactionType.C, transactionValue);

        when(accountService.getAccountByNumber(anyInt())).thenReturn(Optional.of(account));
        when(account.getBalance()).thenReturn(BigDecimal.valueOf(104.99));

        assertThrows(InsufficientBalanceException.class, () -> {
            transactionService.processTransaction(transaction);
        });
    }

    @Test
    @DisplayName("Deve lançar exceção quando a conta não existir")
    void shouldThrowExceptionWhenAccountDoesNotExist() {
        BigDecimal transactionValue = BigDecimal.valueOf(100);

        Transaction transaction = new Transaction(account, TransactionType.P, transactionValue);

        when(account.getAccountNumber()).thenReturn(234);
        when(accountService.getAccountByNumber(234)).thenReturn(Optional.empty());

        assertThrows(AccountNotFoundException.class, () -> {
            transactionService.processTransaction(transaction);
        });
    }
}