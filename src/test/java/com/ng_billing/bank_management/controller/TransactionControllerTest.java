package com.ng_billing.bank_management.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ng_billing.bank_management.domain.*;
import com.ng_billing.bank_management.infra.exceptions.InsufficientBalanceException;
import com.ng_billing.bank_management.service.AccountService;
import com.ng_billing.bank_management.service.TransactionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
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
    private TransactionService transactionService;

    @MockBean
    private AccountService accountService;

    @Autowired
    private MockMvc mockMvc;

    private ObjectMapper objectMapper;
    private Account account;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        objectMapper = new ObjectMapper();
        account = new Account(123, BigDecimal.valueOf(500.00));
    }

    @Test
    @DisplayName("Deve processar uma transação de débito e retornar 201 Created")
    void shouldProcessDebitTransactionSuccessfully() throws Exception {
        TransactionDTO transactionDTO = new TransactionDTO(TransactionType.D, 123, BigDecimal.valueOf(100.00));

        when(accountService.getAccountByNumber(123)).thenReturn(Optional.of(account));

        mockMvc.perform(post("/transacao")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(transactionDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.numero_conta").value(123))
                .andExpect(jsonPath("$.saldo").exists());

        verify(accountService, times(2)).getAccountByNumber(123);
        verify(transactionService, times(1)).processTransaction(any(Transaction.class));
    }

    @Test
    @DisplayName("Deve processar uma transação de crédito e retornar 201 Created")
    void shouldProcessCreditTransactionSuccessfully() throws Exception {
        TransactionDTO transactionDTO = new TransactionDTO(TransactionType.C, 123, BigDecimal.valueOf(200.00));

        when(accountService.getAccountByNumber(123)).thenReturn(Optional.of(account));

        mockMvc.perform(post("/transacao")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(transactionDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.numero_conta").value(123))
                .andExpect(jsonPath("$.saldo").exists());

        verify(accountService, times(2)).getAccountByNumber(123);
        verify(transactionService, times(1)).processTransaction(any(Transaction.class));
    }

    @Test
    @DisplayName("Deve processar uma transação Pix e retornar 201 Created")
    void shouldProcessPixTransactionSuccessfully() throws Exception {
        TransactionDTO transactionDTO = new TransactionDTO(TransactionType.P, 123, BigDecimal.valueOf(50.00));

        when(accountService.getAccountByNumber(123)).thenReturn(Optional.of(account));

        mockMvc.perform(post("/transacao")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(transactionDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.numero_conta").value(123))
                .andExpect(jsonPath("$.saldo").exists());

        verify(accountService, times(2)).getAccountByNumber(123);
        verify(transactionService, times(1)).processTransaction(any(Transaction.class));
    }

    @Test
    @DisplayName("Deve retornar 404 Not Found quando a conta não existir")
    void shouldReturnNotFoundWhenAccountDoesNotExist() throws Exception {
        TransactionDTO transactionDTO = new TransactionDTO(TransactionType.D, 123, BigDecimal.valueOf(100.00));

        when(accountService.getAccountByNumber(123)).thenReturn(Optional.empty());

        mockMvc.perform(post("/transacao")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(transactionDTO)))
                .andExpect(status().isNotFound());

        verify(accountService, times(1)).getAccountByNumber(123);
        verify(transactionService, never()).processTransaction(any(Transaction.class));
    }

    @Test
    @DisplayName("Deve retornar 400 Bad Request quando a transação for inválida")
    void shouldReturnBadRequestWhenTransactionIsInvalid() throws Exception {
        TransactionDTO invalidTransactionDTO = new TransactionDTO(TransactionType.D, 0, BigDecimal.valueOf(-50));

        mockMvc.perform(post("/transacao")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidTransactionDTO)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Deve retornar 400 Bad Request quando o saldo for insuficiente")
    void shouldReturnBadRequestWhenInsufficientBalance() throws Exception {
        TransactionDTO transactionDTO = new TransactionDTO(TransactionType.P, 123, BigDecimal.valueOf(501));

        when(accountService.getAccountByNumber(123)).thenReturn(Optional.of(account));

        doThrow(new InsufficientBalanceException("Saldo insuficiente para realizar a transação."))
                .when(transactionService).processTransaction(any(Transaction.class));


        mockMvc.perform(post("/transacao")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(transactionDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("Saldo insuficiente para realizar a transação.")));

        verify(accountService, times(1)).getAccountByNumber(123);
        verify(transactionService, times(1)).processTransaction(any(Transaction.class));
    }
}
