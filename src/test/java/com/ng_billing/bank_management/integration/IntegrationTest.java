package com.ng_billing.bank_management.integration;

import com.ng_billing.bank_management.domain.Account;
import com.ng_billing.bank_management.domain.Transaction;
import com.ng_billing.bank_management.domain.TransactionType;
import com.ng_billing.bank_management.infra.exceptions.AccountAlreadyExistsException;
import com.ng_billing.bank_management.infra.exceptions.AccountNotFoundException;
import com.ng_billing.bank_management.infra.exceptions.InsufficientBalanceException;
import com.ng_billing.bank_management.repository.AccountRepository;
import com.ng_billing.bank_management.repository.TransactionRepository;
import com.ng_billing.bank_management.service.AccountService;
import com.ng_billing.bank_management.service.TransactionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@Transactional
@ActiveProfiles("test")
public class IntegrationTest {

    @Autowired
    private AccountService accountService;

    @Autowired
    private TransactionService transactionService;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    private Account testAccount;

    @BeforeEach
    void setup() {
        testAccount = new Account(123, BigDecimal.valueOf(1000));
        accountRepository.save(testAccount);
    }

    @Test
    @DisplayName("Deve criar uma conta com sucesso")
    void shouldCreateAccountSuccessfully() {
        Account newAccount = new Account(234, BigDecimal.valueOf(500));
        accountService.createAccount(newAccount);

        Optional<Account> createdAccount = accountService.getAccountByNumber(234);
        assertTrue(createdAccount.isPresent());
        assertEquals(BigDecimal.valueOf(500), createdAccount.get().getBalance());
    }

    @Test
    @DisplayName("Deve lançar AccountAlreadyExistsException ao tentar criar uma conta com número duplicado")
    void shouldThrowAccountAlreadyExistsExceptionWhenAccountAlreadyExists() {
        Account duplicateAccount = new Account(123, BigDecimal.valueOf(500));

        assertThrows(AccountAlreadyExistsException.class, () -> {
            accountService.createAccount(duplicateAccount);
        });
    }

    @Test
    @DisplayName("Deve retornar a conta existente")
    void shouldReturnExistingAccount() {
        Optional<Account> foundAccount = accountService.getAccountByNumber(123);
        assertTrue(foundAccount.isPresent());
        assertEquals(123, foundAccount.get().getAccountNumber());
    }

    @Test
    @DisplayName("Deve retornar Optional vazio quando a conta não existir")
    void shouldReturnEmptyWhenAccountDoesNotExist() {
        Optional<Account> foundAccount = accountService.getAccountByNumber(999);
        assertTrue(foundAccount.isEmpty());
    }

    @Test
    @DisplayName("Deve processar transação de crédito com sucesso")
    void shouldProcessCreditTransactionSuccessfully() throws AccountNotFoundException {
        Transaction transaction = new Transaction(testAccount, TransactionType.C, BigDecimal.valueOf(200));
        transactionService.processTransaction(transaction);

        Account updatedAccount = accountService.getAccountByNumber(123).orElseThrow();
        assertEquals(BigDecimal.valueOf(790).setScale(2, RoundingMode.HALF_UP), updatedAccount.getBalance());
    }

    @Test
    @DisplayName("Deve processar transação de débito com sucesso")
    void shouldProcessDebitTransactionSuccessfully() throws AccountNotFoundException {
        Transaction transaction = new Transaction(testAccount, TransactionType.D, BigDecimal.valueOf(300));
        transactionService.processTransaction(transaction);

        Account updatedAccount = accountService.getAccountByNumber(123).orElseThrow();
        assertEquals(BigDecimal.valueOf(691).setScale(2, RoundingMode.HALF_UP), updatedAccount.getBalance());
    }

    @Test
    @DisplayName("Deve processar transação de Pix com sucesso")
    void shouldProcessPixTransactionSuccessfully() throws AccountNotFoundException {
        Transaction transaction = new Transaction(testAccount, TransactionType.P, BigDecimal.valueOf(999));
        transactionService.processTransaction(transaction);

        Account updatedAccount = accountService.getAccountByNumber(123).orElseThrow();
        assertEquals(BigDecimal.valueOf(1).setScale(2, RoundingMode.HALF_UP), updatedAccount.getBalance().setScale(2, RoundingMode.HALF_UP));
    }

    @Test
    @DisplayName("Deve lançar InsufficientBalanceException quando o saldo for insuficiente para a transação")
    void shouldThrowInsufficientBalanceExceptionWhenInsufficientBalance() {
        Transaction transaction = new Transaction(testAccount, TransactionType.D, BigDecimal.valueOf(5000));

        assertThrows(InsufficientBalanceException.class, () -> {
            transactionService.processTransaction(transaction);
        });
    }

    @Test
    @DisplayName("Deve lançar AccountNotFoundException quando a conta não existir para a transação")
    void shouldThrowAccountNotFoundExceptionWhenAccountDoesNotExist() {
        Account invalidAccount = new Account(999, BigDecimal.valueOf(100));
        Transaction transaction = new Transaction(invalidAccount, TransactionType.C, BigDecimal.valueOf(100));

        assertThrows(AccountNotFoundException.class, () -> {
            transactionService.processTransaction(transaction);
        });
    }

    @Test
    @DisplayName("Deve processar duas transações seguidas com sucesso")
    void shouldProcessTwoTransactionsSuccessfully() throws AccountNotFoundException {
        Transaction firstTransaction = new Transaction(testAccount, TransactionType.C, BigDecimal.valueOf(200));
        transactionService.processTransaction(firstTransaction);

        Account accountAfterFirstTransaction = accountService.getAccountByNumber(123).orElseThrow();
        assertEquals(BigDecimal.valueOf(790).setScale(2, RoundingMode.HALF_UP), accountAfterFirstTransaction.getBalance());

        Transaction secondTransaction = new Transaction(testAccount, TransactionType.D, BigDecimal.valueOf(100));
        transactionService.processTransaction(secondTransaction);

        Account accountAfterSecondTransaction = accountService.getAccountByNumber(123).orElseThrow();
        assertEquals(BigDecimal.valueOf(687).setScale(2, RoundingMode.HALF_UP), accountAfterSecondTransaction.getBalance());
    }

    @Test
    @DisplayName("Deve processar uma transação e falhar na segunda por saldo insuficiente")
    void shouldFailOnSecondTransactionDueToInsufficientBalance() throws AccountNotFoundException {
        Transaction firstTransaction = new Transaction(testAccount, TransactionType.C, BigDecimal.valueOf(200));
        transactionService.processTransaction(firstTransaction);

        Account accountAfterFirstTransaction = accountService.getAccountByNumber(123).orElseThrow();
        assertEquals(BigDecimal.valueOf(790).setScale(2, RoundingMode.HALF_UP), accountAfterFirstTransaction.getBalance());

        Transaction secondTransaction = new Transaction(testAccount, TransactionType.D, BigDecimal.valueOf(790));

        assertThrows(InsufficientBalanceException.class, () -> {
            transactionService.processTransaction(secondTransaction);
        });

        Account accountAfterFailedTransaction = accountService.getAccountByNumber(123).orElseThrow();
        assertEquals(BigDecimal.valueOf(790).setScale(2, RoundingMode.HALF_UP), accountAfterFailedTransaction.getBalance());
    }

}
