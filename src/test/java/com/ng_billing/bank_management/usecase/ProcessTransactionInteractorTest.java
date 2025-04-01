package com.ng_billing.bank_management.usecase;

import com.ng_billing.bank_management.application.usecases.GetAccountEntityByAccountNumberInteractor;
import com.ng_billing.bank_management.application.usecases.ProcessTransactionInteractor;
import com.ng_billing.bank_management.domain.entity.Transaction;
import com.ng_billing.bank_management.domain.strategy.TransactionStrategyFactory;
import com.ng_billing.bank_management.domain.strategy.TransactionTypeStrategy;
import com.ng_billing.bank_management.infra.exceptions.InsufficientBalanceException;
import com.ng_billing.bank_management.infra.persistence.AccountEntity;
import com.ng_billing.bank_management.infra.persistence.AccountRepository;
import com.ng_billing.bank_management.application.gateways.TransactionGateway;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProcessTransactionInteractorTest {

    @InjectMocks
    private ProcessTransactionInteractor processTransactionInteractor;

    @Mock
    private TransactionGateway transactionGateway;

    @Mock
    private TransactionStrategyFactory strategyFactory;

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private TransactionTypeStrategy transactionTypeStrategy;

    @Mock
    private AccountEntity accountEntity;

    private Transaction transaction;

    @BeforeEach
    void setUp() {
        accountEntity = new AccountEntity(234, BigDecimal.valueOf(150));

        transaction = new Transaction(accountEntity, Transaction.TransactionType.D, BigDecimal.valueOf(100));
    }

    @Test
    @DisplayName("Deve criar uma transação quando o saldo for suficiente")
    void shouldCreateTransactionWhenBalanceIsSufficient() {
        when(accountRepository.findByAccountNumber(234)).thenReturn(accountEntity);
        when(strategyFactory.getStrategy(Transaction.TransactionType.D)).thenReturn(transactionTypeStrategy);
        when(transactionTypeStrategy.calculateTotalAmount(transaction.amount())).thenReturn(BigDecimal.valueOf(100));

        when(transactionGateway.createTransaction(any(Transaction.class))).thenReturn(transaction);

        Transaction result = processTransactionInteractor.createTransaction(transaction);

        assertNotNull(result);
        assertEquals(transaction, result);
        verify(accountRepository).findByAccountNumber(234);
        verify(transactionGateway).createTransaction(transaction);
    }

    @Test
    @DisplayName("Deve lançar InsufficientBalanceException quando o saldo for insuficiente")
    void shouldThrowInsufficientBalanceExceptionWhenBalanceIsInsufficient() {
        when(accountRepository.findByAccountNumber(234)).thenReturn(accountEntity);
        when(strategyFactory.getStrategy(Transaction.TransactionType.D)).thenReturn(transactionTypeStrategy);
        when(transactionTypeStrategy.calculateTotalAmount(transaction.amount())).thenReturn(BigDecimal.valueOf(200)); // Valor maior que o saldo

        InsufficientBalanceException thrown = assertThrows(InsufficientBalanceException.class, () -> {
            processTransactionInteractor.createTransaction(transaction);
        });

        assertEquals("Saldo insuficiente para realizar a transação.", thrown.getMessage());
    }
}
