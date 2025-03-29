package com.ng_billing.bank_management.service;

import com.ng_billing.bank_management.domain.Account;
import com.ng_billing.bank_management.domain.Transaction;
import com.ng_billing.bank_management.domain.TransactionType;
import com.ng_billing.bank_management.exceptions.InsufficientBalanceException;
import com.ng_billing.bank_management.repository.TransactionRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.security.auth.login.AccountNotFoundException;

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
        float transactionValue = 100.00f;
        float fee = transactionValue * 0.03f;
        float totalAmount = transactionValue + fee;

        Transaction transaction = new Transaction(account, TransactionType.D, transactionValue);

        when(account.getBalance()).thenReturn(103.00f);
        when(accountService.getAccountByNumber(anyInt())).thenReturn(Optional.of(account));
        when(transactionRepository.save(any(Transaction.class))).thenReturn(transaction);

        Transaction result = transactionService.processTransaction(transaction);

        verify(transactionRepository).save(transaction);
        assertNotNull(result);
        assertEquals(transactionValue, result.getValue());
        assertEquals(TransactionType.D, result.getType());
        assertEquals(totalAmount, account.getBalance(), 0.01f);
    }

    @Test
    @DisplayName("Deve realizar a transação de crédito com sucesso quando a conta tiver saldo suficiente")
    void shouldCompleteCreditTransactionWhenAccountHasSufficientBalance() throws AccountNotFoundException, InsufficientBalanceException {
        float transactionValue = 100.00f;
        float fee = transactionValue * 0.05f;
        float totalAmount = transactionValue + fee;

        when(account.getBalance()).thenReturn(1000.00f);
        when(accountService.getAccountByNumber(anyInt())).thenReturn(Optional.of(account));
        when(transactionRepository.save(any(Transaction.class))).thenReturn(transaction);

        Transaction result = transactionService.processTransaction(transaction);

        verify(transactionRepository).save(transaction);
        assertNotNull(result);
        assertEquals(transactionValue, result.getValue());
        assertEquals(TransactionType.C, result.getType());
        assertEquals(totalAmount, account.getBalance(), 0.01f);
    }

    @Test
    @DisplayName("Deve realizar a transação de Pix com sucesso quando a conta tiver saldo suficiente")
    void shouldCompletePixTransactionWhenAccountHasSufficientBalance() throws AccountNotFoundException, InsufficientBalanceException {
        float transactionValue = 100.00f;
        float totalAmount = transactionValue;

        when(account.getBalance()).thenReturn(1000.00f);
        when(accountService.getAccountByNumber(anyInt())).thenReturn(Optional.of(account));
        when(transactionRepository.save(any(Transaction.class))).thenReturn(transaction);

        Transaction result = transactionService.processTransaction(transaction);

        verify(transactionRepository).save(transaction);
        assertNotNull(result);
        assertEquals(transactionValue, result.getValue());
        assertEquals(TransactionType.P, result.getType());
        assertEquals(totalAmount, account.getBalance(), 0.01f);
    }

    @Test
    @DisplayName("Deve lançar exceção quando a conta não tiver saldo suficiente")
    void shouldThrowExceptionWhenAccountHasInsufficientBalance() {
        when(accountService.getAccountByNumber(anyInt())).thenReturn(Optional.of(account));
        when(account.getBalance()).thenReturn(100.00f);

        assertThrows(InsufficientBalanceException.class, () -> {
            transactionService.processTransaction(transaction);
        });
    }

    @Test
    @DisplayName("Deve lançar exceção quando a conta não existir")
    void shouldThrowExceptionWhenAccountDoesNotExist() {
        when(account.getAccountNumber()).thenReturn(234);
        when(accountService.getAccountByNumber(234)).thenReturn(Optional.empty());

        assertThrows(AccountNotFoundException.class, () -> {
            transactionService.processTransaction(transaction);
        });
    }
}