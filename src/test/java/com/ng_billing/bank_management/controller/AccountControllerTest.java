package com.ng_billing.bank_management.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ng_billing.bank_management.application.usecases.CreateAccountInteractor;
import com.ng_billing.bank_management.application.usecases.GetAccountByAccountNumberInteractor;
import com.ng_billing.bank_management.domain.entity.Account;
import com.ng_billing.bank_management.infra.controllers.AccountController;
import com.ng_billing.bank_management.infra.controllers.AccountDTO;
import com.ng_billing.bank_management.infra.exceptions.AccountAlreadyExistsException;
import com.ng_billing.bank_management.infra.exceptions.AccountNotFoundException;
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

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AccountController.class)
class AccountControllerTest {

    @InjectMocks
    private AccountController accountController;

    @MockBean
    private CreateAccountInteractor createAccountInteractor;

    @MockBean
    private GetAccountByAccountNumberInteractor getAccountByAccountNumberInteractor;

    @Autowired
    private MockMvc mockMvc;

    private ObjectMapper objectMapper;
    private Account account;
    private AccountDTO accountDTO;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        account = new Account(234, BigDecimal.valueOf(170.07));
        accountDTO = new AccountDTO(account.accountNumber(), account.balance());
    }

    @Test
    @DisplayName("Deve retornar detalhes da conta quando a conta existir")
    void shouldReturnAccountWhenAccountExists() throws Exception {
        when(getAccountByAccountNumberInteractor.getAccountByAccountNumber(234)).thenReturn(account);

        mockMvc.perform(get("/conta?numero_conta={accountNumber}", 234))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.numero_conta").value(234))
                .andExpect(jsonPath("$.saldo").value(170.07));

        verify(getAccountByAccountNumberInteractor, times(1)).getAccountByAccountNumber(234);
    }

    @Test
    @DisplayName("Deve retornar 404 Not Found quando a conta não existir")
    void shouldReturnNotFoundWhenAccountDoesNotExist() throws Exception {
        when(getAccountByAccountNumberInteractor.getAccountByAccountNumber(234))
                .thenThrow(new AccountNotFoundException(234));

        mockMvc.perform(get("/conta?numero_conta={accountNumber}", 234))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Conta não encontrada para o número: 234"));

        verify(getAccountByAccountNumberInteractor, times(1)).getAccountByAccountNumber(234);
    }

    @Test
    @DisplayName("Deve retornar 201 Created e JSON com detalhes da conta quando a conta for cadastrada com sucesso")
    void shouldReturnCreatedWhenAccountIsSuccessfullyCreated() throws Exception {
        when(createAccountInteractor.createAccount(any())).thenReturn(account);

        mockMvc.perform(post("/conta")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(accountDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.numero_conta").value(234))
                .andExpect(jsonPath("$.saldo").value(170.07));

        verify(createAccountInteractor, times(1)).createAccount(any());
    }

    @Test
    @DisplayName("Deve retornar 400 Bad Request quando a tentar cadastrar conta já existente")
    void shouldReturnBadRequestWhenAccountAlreadyExists() throws Exception {
        when(createAccountInteractor.createAccount(any(Account.class)))
                .thenThrow(new AccountAlreadyExistsException(234));

        mockMvc.perform(post("/conta")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(accountDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Conta de número 234 já existente."));

        verify(createAccountInteractor, times(1)).createAccount(any());
    }

    @Test
    @DisplayName("Deve retornar 400 Bad Request quando dados inválidos forem fornecidos")
    void shouldReturnBadRequestWhenInvalidDataProvided() throws Exception {
        AccountDTO invalidAccountDTO = new AccountDTO(0, BigDecimal.valueOf(-100));

        mockMvc.perform(post("/conta")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidAccountDTO)))
                .andExpect(status().isBadRequest());
    }
}
