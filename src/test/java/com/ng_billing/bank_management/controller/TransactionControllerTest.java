package com.ng_billing.bank_management.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ng_billing.bank_management.application.usecases.GetAccountEntityByAccountNumberInteractor;
import com.ng_billing.bank_management.application.usecases.ProcessTransactionInteractor;
import com.ng_billing.bank_management.domain.entity.Transaction;
import com.ng_billing.bank_management.infra.controllers.TransactionController;
import com.ng_billing.bank_management.infra.controllers.TransactionDTO;
import com.ng_billing.bank_management.infra.exceptions.AccountNotFoundException;
import com.ng_billing.bank_management.infra.exceptions.InsufficientBalanceException;
import com.ng_billing.bank_management.infra.persistence.AccountEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Optional;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TransactionController.class)
class TransactionControllerTest {

    @InjectMocks
    private TransactionController transactionController;

    @MockBean
    private ProcessTransactionInteractor processTransactionInteractor;

    @MockBean
    private GetAccountEntityByAccountNumberInteractor getAccountEntityByAccountNumberInteractor;

    @Autowired
    private MockMvc mockMvc;

    private ObjectMapper objectMapper;
    private AccountEntity accountEntity;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        accountEntity = new AccountEntity(123, BigDecimal.valueOf(500.00));  // Usando AccountEntity, que agora é o tipo correto
    }

    @Test
    @DisplayName("Deve processar uma transação de débito e retornar 201 Created")
    void shouldProcessDebitTransactionSuccessfully() throws Exception {
        BigDecimal initialBalance = accountEntity.getBalance();
        BigDecimal transactionAmount = BigDecimal.valueOf(100.00);
        BigDecimal totalTransactionAmount = transactionAmount.multiply(BigDecimal.valueOf(1.03));

        TransactionDTO transactionDTO = new TransactionDTO(Transaction.TransactionType.C, 123, totalTransactionAmount);

        AccountEntity updatedAccountEntity = new AccountEntity(123, BigDecimal.valueOf(397.00));

        when(getAccountEntityByAccountNumberInteractor.getAccountEntityByAccountNumber(123))
                .thenReturn(accountEntity)
                .thenReturn(updatedAccountEntity);

        mockMvc.perform(post("/transacao")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(transactionDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.numero_conta").value(123))
                .andExpect(jsonPath("$.saldo").value(initialBalance.subtract(totalTransactionAmount).setScale(2, RoundingMode.HALF_UP).doubleValue()));

        verify(getAccountEntityByAccountNumberInteractor, times(2)).getAccountEntityByAccountNumber(123);
        verify(processTransactionInteractor, times(1)).createTransaction(any(Transaction.class));
    }

    @Test
    @DisplayName("Deve processar uma transação de crédito e retornar 201 Created")
    void shouldProcessCreditTransactionSuccessfully() throws Exception {
        BigDecimal initialBalance = accountEntity.getBalance();
        BigDecimal transactionAmount = BigDecimal.valueOf(100.00);
        BigDecimal totalTransactionAmount = transactionAmount.multiply(BigDecimal.valueOf(1.05));

        TransactionDTO transactionDTO = new TransactionDTO(Transaction.TransactionType.C, 123, totalTransactionAmount);

        AccountEntity updatedAccountEntity = new AccountEntity(123, BigDecimal.valueOf(395.00));

        when(getAccountEntityByAccountNumberInteractor.getAccountEntityByAccountNumber(123))
                .thenReturn(accountEntity)
                .thenReturn(updatedAccountEntity);

        mockMvc.perform(post("/transacao")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(transactionDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.numero_conta").value(123))
                .andExpect(jsonPath("$.saldo").value(initialBalance.subtract(totalTransactionAmount).setScale(2, RoundingMode.HALF_UP).doubleValue()));

        verify(getAccountEntityByAccountNumberInteractor, times(2)).getAccountEntityByAccountNumber(123);
        verify(processTransactionInteractor, times(1)).createTransaction(any(Transaction.class));
    }

    @Test
    @DisplayName("Deve processar uma transação Pix e retornar 201 Created")
    void shouldProcessPixTransactionSuccessfully() throws Exception {
        BigDecimal initialBalance = accountEntity.getBalance();
        BigDecimal transactionAmount = BigDecimal.valueOf(50.00);

        AccountEntity updatedAccountEntity = new AccountEntity(123, BigDecimal.valueOf(450.00));

        TransactionDTO transactionDTO = new TransactionDTO(Transaction.TransactionType.P, 123, transactionAmount);

        when(getAccountEntityByAccountNumberInteractor.getAccountEntityByAccountNumber(123))
                .thenReturn(accountEntity)
                .thenReturn(updatedAccountEntity);

        mockMvc.perform(post("/transacao")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(transactionDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.numero_conta").value(123))
                .andExpect(jsonPath("$.saldo").value(initialBalance.subtract(transactionAmount)));

        verify(getAccountEntityByAccountNumberInteractor, times(2)).getAccountEntityByAccountNumber(123);
        verify(processTransactionInteractor, times(1)).createTransaction(any(Transaction.class));
    }

    @Test
    @DisplayName("Deve retornar 404 Not Found quando a conta não existir")
    void shouldReturnNotFoundWhenAccountDoesNotExist() throws Exception {
        TransactionDTO transactionDTO = new TransactionDTO(Transaction.TransactionType.D, 123, BigDecimal.valueOf(100.00));

        when(getAccountEntityByAccountNumberInteractor.getAccountEntityByAccountNumber(123)).thenThrow(AccountNotFoundException.class);

        mockMvc.perform(post("/transacao")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(transactionDTO)))
                .andExpect(status().isNotFound());

        verify(getAccountEntityByAccountNumberInteractor, times(1)).getAccountEntityByAccountNumber(123);
        verify(processTransactionInteractor, never()).createTransaction(any(Transaction.class));
    }

    @Test
    @DisplayName("Deve retornar 400 Bad Request quando a transação for inválida")
    void shouldReturnBadRequestWhenTransactionIsInvalid() throws Exception {
        TransactionDTO invalidTransactionDTO = new TransactionDTO(Transaction.TransactionType.D, 0, BigDecimal.valueOf(-50));

        mockMvc.perform(post("/transacao")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidTransactionDTO)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Deve retornar 404 Not Found quando o saldo for insuficiente")
    void shouldReturnNotFoundWhenInsufficientBalance() throws Exception {
        TransactionDTO transactionDTO = new TransactionDTO(Transaction.TransactionType.P, 123, BigDecimal.valueOf(501));

        when(getAccountEntityByAccountNumberInteractor.getAccountEntityByAccountNumber(123)).thenReturn(accountEntity);

        doThrow(new InsufficientBalanceException()).when(processTransactionInteractor).createTransaction(any(Transaction.class));

        mockMvc.perform(post("/transacao")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(transactionDTO)))
                .andExpect(status().isNotFound())
                .andExpect(content().string(containsString("Saldo insuficiente para realizar a transação.")));

        verify(getAccountEntityByAccountNumberInteractor, times(1)).getAccountEntityByAccountNumber(123);
        verify(processTransactionInteractor, times(1)).createTransaction(any(Transaction.class));
    }
}


//@WebMvcTest(TransactionController.class)
//class TransactionControllerTest {
//
//    @InjectMocks
//    private TransactionController transactionController;
//
//    @MockBean
//    private TransactionService transactionService;
//
//    @MockBean
//    private AccountService accountService;
//
//    @Autowired
//    private MockMvc mockMvc;
//
//    private ObjectMapper objectMapper;
//    private Account account;
//
//    @BeforeEach
//    void setUp() {
//        MockitoAnnotations.openMocks(this);
//
//        objectMapper = new ObjectMapper();
//        account = new Account(123, BigDecimal.valueOf(500.00));
//    }
//
//    @Test
//    @DisplayName("Deve processar uma transação de débito e retornar 201 Created")
//    void shouldProcessDebitTransactionSuccessfully() throws Exception {
//        BigDecimal initialBalance = account.getBalance();
//        BigDecimal transactionAmount = BigDecimal.valueOf(100.00);
//        BigDecimal totalTransactionAmount = transactionAmount.multiply(BigDecimal.valueOf(1.03));
//
//        TransactionDTO transactionDTO = new TransactionDTO(Transaction.TransactionType.C, 123, totalTransactionAmount);
//
//        Account updatedAccount = new Account(123, BigDecimal.valueOf(397.00).setScale(2, RoundingMode.HALF_UP));
//
//        when(accountService.getAccountByNumber(123))
//                .thenReturn(Optional.of(account))
//                .thenReturn(Optional.of(updatedAccount));
//
//        mockMvc.perform(post("/transacao")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(transactionDTO)))
//                .andExpect(status().isCreated())
//                .andExpect(jsonPath("$.numero_conta").value(123))
//                .andExpect(jsonPath("$.saldo")
//                        .value(initialBalance.subtract(totalTransactionAmount)
//                                .setScale(2, RoundingMode.HALF_UP).
//                                doubleValue()));
//
//        verify(accountService, times(2)).getAccountByNumber(123);
//        verify(transactionService, times(1)).processTransaction(any(Transaction.class));
//    }
//
//    @Test
//    @DisplayName("Deve processar uma transação de crédito e retornar 201 Created")
//    void shouldProcessCreditTransactionSuccessfully() throws Exception {
//        BigDecimal initialBalance = account.getBalance();
//        BigDecimal transactionAmount = BigDecimal.valueOf(100.00);
//        BigDecimal totalTransactionAmount = transactionAmount.multiply(BigDecimal.valueOf(1.05));
//
//        TransactionDTO transactionDTO = new TransactionDTO(Transaction.TransactionType.C, 123, totalTransactionAmount);
//
//        Account updatedAccount = new Account(123, BigDecimal.valueOf(395.00).setScale(2, RoundingMode.HALF_UP));
//
//        when(accountService.getAccountByNumber(123))
//                .thenReturn(Optional.of(account))
//                .thenReturn(Optional.of(updatedAccount));
//
//        mockMvc.perform(post("/transacao")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(transactionDTO)))
//                .andExpect(status().isCreated())
//                .andExpect(jsonPath("$.numero_conta").value(123))
//                .andExpect(jsonPath("$.saldo")
//                        .value(initialBalance.subtract(totalTransactionAmount)
//                                .setScale(2, RoundingMode.HALF_UP).
//                                doubleValue()));
//
//        verify(accountService, times(2)).getAccountByNumber(123);
//        verify(transactionService, times(1)).processTransaction(any(Transaction.class));
//    }
//
//    @Test
//    @DisplayName("Deve processar uma transação Pix e retornar 201 Created")
//    void shouldProcessPixTransactionSuccessfully() throws Exception {
//        BigDecimal initialBalance = account.getBalance();
//        BigDecimal transactionAmount = BigDecimal.valueOf(50.00);
//
//        Account updatedAccount = new Account(123, BigDecimal.valueOf(450.00));
//
//        TransactionDTO transactionDTO = new TransactionDTO(Transaction.TransactionType.P, 123, transactionAmount);
//
//        when(accountService.getAccountByNumber(123))
//                .thenReturn(Optional.of(account))
//                .thenReturn(Optional.of(updatedAccount));
//
//        mockMvc.perform(post("/transacao")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(transactionDTO)))
//                .andExpect(status().isCreated())
//                .andExpect(jsonPath("$.numero_conta").value(123))
//                .andExpect(jsonPath("$.saldo").value(initialBalance.subtract(transactionAmount)));
//
//        verify(accountService, times(2)).getAccountByNumber(123);
//        verify(transactionService, times(1)).processTransaction(any(Transaction.class));
//    }
//
//    @Test
//    @DisplayName("Deve retornar 404 Not Found quando a conta não existir")
//    void shouldReturnNotFoundWhenAccountDoesNotExist() throws Exception {
//        TransactionDTO transactionDTO = new TransactionDTO(Transaction.TransactionType.D, 123, BigDecimal.valueOf(100.00));
//
//        when(accountService.getAccountByNumber(123)).thenReturn(Optional.empty());
//
//        mockMvc.perform(post("/transacao")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(transactionDTO)))
//                .andExpect(status().isNotFound());
//
//        verify(accountService, times(1)).getAccountByNumber(123);
//        verify(transactionService, never()).processTransaction(any(Transaction.class));
//    }
//
//    @Test
//    @DisplayName("Deve retornar 400 Bad Request quando a transação for inválida")
//    void shouldReturnBadRequestWhenTransactionIsInvalid() throws Exception {
//        TransactionDTO invalidTransactionDTO = new TransactionDTO(Transaction.TransactionType.D, 0, BigDecimal.valueOf(-50));
//
//        mockMvc.perform(post("/transacao")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(invalidTransactionDTO)))
//                .andExpect(status().isBadRequest());
//    }
//
//    @Test
//    @DisplayName("Deve retornar 400 Bad Request quando o saldo for insuficiente")
//    void shouldReturnBadRequestWhenInsufficientBalance() throws Exception {
//        TransactionDTO transactionDTO = new TransactionDTO(Transaction.TransactionType.P, 123, BigDecimal.valueOf(501));
//
//        when(accountService.getAccountByNumber(123)).thenReturn(Optional.of(account));
//
//        doThrow(new InsufficientBalanceException())
//                .when(transactionService).processTransaction(any(Transaction.class));
//
//
//        mockMvc.perform(post("/transacao")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(transactionDTO)))
//                .andExpect(status().isBadRequest())
//                .andExpect(content().string(containsString("Saldo insuficiente para realizar a transação.")));
//
//        verify(accountService, times(1)).getAccountByNumber(123);
//        verify(transactionService, times(1)).processTransaction(any(Transaction.class));
//    }
//}
