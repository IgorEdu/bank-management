package com.ng_billing.bank_management.integration;

import com.ng_billing.bank_management.application.usecases.CreateAccountInteractor;
import com.ng_billing.bank_management.application.usecases.GetAccountEntityByAccountNumberInteractor;
import com.ng_billing.bank_management.application.usecases.ProcessTransactionInteractor;
import com.ng_billing.bank_management.domain.entity.Account;
import com.ng_billing.bank_management.domain.entity.Transaction;
import com.ng_billing.bank_management.infra.exceptions.AccountAlreadyExistsException;
import com.ng_billing.bank_management.infra.exceptions.AccountNotFoundException;
import com.ng_billing.bank_management.infra.exceptions.InsufficientBalanceException;
import com.ng_billing.bank_management.infra.persistence.AccountEntity;
import com.ng_billing.bank_management.infra.persistence.AccountRepository;
import com.ng_billing.bank_management.infra.persistence.TransactionRepository;
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

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@Transactional
@ActiveProfiles("test")
public class IntegrationTest {

    @Autowired
    private CreateAccountInteractor createAccountInteractor;

    @Autowired
    private GetAccountEntityByAccountNumberInteractor getAccountEntityByAccountNumberInteractor;

    @Autowired
    private ProcessTransactionInteractor processTransactionInteractor;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    private AccountEntity testAccount;

    @BeforeEach
    void setup() {
        testAccount = new AccountEntity(123, BigDecimal.valueOf(1000));
        accountRepository.save(testAccount);
    }

    @Test
    @DisplayName("Deve criar uma conta com sucesso")
    void shouldCreateAccountSuccessfully() {
        Account newAccount = new Account(789, BigDecimal.valueOf(500));
        createAccountInteractor.createAccount(newAccount);

        AccountEntity createdAccount = getAccountEntityByAccountNumberInteractor.getAccountEntityByAccountNumber(789);
        assertNotNull(createdAccount);
        assertEquals(BigDecimal.valueOf(500), createdAccount.getBalance());
    }

    @Test
    @DisplayName("Deve lançar AccountAlreadyExistsException ao tentar criar uma conta com número duplicado")
    void shouldThrowAccountAlreadyExistsExceptionWhenAccountAlreadyExists() {
        Account duplicateAccount = new Account(123, BigDecimal.valueOf(500));

        assertThrows(AccountAlreadyExistsException.class, () -> {
            createAccountInteractor.createAccount(duplicateAccount);
        });
    }

    @Test
    @DisplayName("Deve retornar a conta existente")
    void shouldReturnExistingAccount() {
        AccountEntity foundAccount = getAccountEntityByAccountNumberInteractor.getAccountEntityByAccountNumber(123);
        assertNotNull(foundAccount);
        assertEquals(123, foundAccount.getAccountNumber());
    }

    @Test
    @DisplayName("Deve lançar AccountNotFoundException quando a conta não existir")
    void shouldThrowAccountNotFoundExceptionWhenAccountDoesNotExist() {
        assertThrows(AccountNotFoundException.class, () -> {
            getAccountEntityByAccountNumberInteractor.getAccountEntityByAccountNumber(999);
        });
    }

    @Test
    @DisplayName("Deve processar transação de crédito com sucesso")
    void shouldProcessCreditTransactionSuccessfully() {
        Transaction transaction = new Transaction(testAccount, Transaction.TransactionType.C, BigDecimal.valueOf(200));
        processTransactionInteractor.createTransaction(transaction);

        AccountEntity updatedAccount = getAccountEntityByAccountNumberInteractor.getAccountEntityByAccountNumber(123);
        assertEquals(BigDecimal.valueOf(790).setScale(2, RoundingMode.HALF_UP), updatedAccount.getBalance());
    }

    @Test
    @DisplayName("Deve processar transação de débito com sucesso")
    void shouldProcessDebitTransactionSuccessfully() {
        Transaction transaction = new Transaction(testAccount, Transaction.TransactionType.D, BigDecimal.valueOf(300));
        processTransactionInteractor.createTransaction(transaction);

        AccountEntity updatedAccount = getAccountEntityByAccountNumberInteractor.getAccountEntityByAccountNumber(123);
        assertEquals(BigDecimal.valueOf(691).setScale(2, RoundingMode.HALF_UP), updatedAccount.getBalance());
    }

    @Test
    @DisplayName("Deve processar transação de Pix com sucesso")
    void shouldProcessPixTransactionSuccessfully() {
        Transaction transaction = new Transaction(testAccount, Transaction.TransactionType.P, BigDecimal.valueOf(999));
        processTransactionInteractor.createTransaction(transaction);

        AccountEntity updatedAccount = getAccountEntityByAccountNumberInteractor.getAccountEntityByAccountNumber(123);
        assertEquals(BigDecimal.valueOf(1).setScale(2, RoundingMode.HALF_UP), updatedAccount.getBalance().setScale(2, RoundingMode.HALF_UP));
    }

    @Test
    @DisplayName("Deve lançar InsufficientBalanceException quando o saldo for insuficiente para a transação")
    void shouldThrowInsufficientBalanceExceptionWhenInsufficientBalance() {
        Transaction transaction = new Transaction(testAccount, Transaction.TransactionType.D, BigDecimal.valueOf(5000));

        assertThrows(InsufficientBalanceException.class, () -> {
            processTransactionInteractor.createTransaction(transaction);
        });
    }

    @Test
    @DisplayName("Deve processar duas transações seguidas com sucesso")
    void shouldProcessTwoTransactionsSuccessfully() {
        Transaction firstTransaction = new Transaction(testAccount, Transaction.TransactionType.C, BigDecimal.valueOf(200));
        processTransactionInteractor.createTransaction(firstTransaction);

        AccountEntity accountAfterFirstTransaction = getAccountEntityByAccountNumberInteractor.getAccountEntityByAccountNumber(123);
        assertEquals(BigDecimal.valueOf(790).setScale(2, RoundingMode.HALF_UP), accountAfterFirstTransaction.getBalance());

        Transaction secondTransaction = new Transaction(testAccount, Transaction.TransactionType.D, BigDecimal.valueOf(100));
        processTransactionInteractor.createTransaction(secondTransaction);

        AccountEntity accountAfterSecondTransaction = getAccountEntityByAccountNumberInteractor.getAccountEntityByAccountNumber(123);
        assertEquals(BigDecimal.valueOf(687).setScale(2, RoundingMode.HALF_UP), accountAfterSecondTransaction.getBalance());
    }

    @Test
    @DisplayName("Deve processar uma transação e falhar na segunda por saldo insuficiente")
    void shouldFailOnSecondTransactionDueToInsufficientBalance() {
        Transaction firstTransaction = new Transaction(testAccount, Transaction.TransactionType.C, BigDecimal.valueOf(200));
        processTransactionInteractor.createTransaction(firstTransaction);

        AccountEntity accountAfterFirstTransaction = getAccountEntityByAccountNumberInteractor.getAccountEntityByAccountNumber(123);
        assertEquals(BigDecimal.valueOf(790).setScale(2, RoundingMode.HALF_UP), accountAfterFirstTransaction.getBalance());

        Transaction secondTransaction = new Transaction(testAccount, Transaction.TransactionType.D, BigDecimal.valueOf(790));

        assertThrows(InsufficientBalanceException.class, () -> {
            processTransactionInteractor.createTransaction(secondTransaction);
        });

        AccountEntity accountAfterFailedTransaction = getAccountEntityByAccountNumberInteractor.getAccountEntityByAccountNumber(123);
        assertEquals(BigDecimal.valueOf(790).setScale(2, RoundingMode.HALF_UP), accountAfterFailedTransaction.getBalance());
    }
}