package com.ng_billing.bank_management.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ng_billing.bank_management.domain.Account;
import com.ng_billing.bank_management.domain.AccountDTO;
import com.ng_billing.bank_management.exceptions.AccountAlreadyExistsException;
import com.ng_billing.bank_management.service.AccountService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class AccountControllerTest {

    @InjectMocks
    private AccountController accountController;

    @MockBean
    private AccountService accountService;

    @Autowired
    private MockMvc mockMvc;

    private ObjectMapper objectMapper;
    private Account account;
    private AccountDTO accountDTO;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        objectMapper = new ObjectMapper();
        account = new Account(234, 170.07f);
        accountDTO = new AccountDTO(account.getAccountNumber(), account.getBalance());
    }

    @Test
    @DisplayName("Deve retornar detalhes da conta quando a conta existir")
    void shouldReturnAccountWhenAccountExists() throws Exception {
        when(accountService.getAccountByNumber(234)).thenReturn(Optional.of(account));

        mockMvc.perform(get("/conta?numero_conta={accountNumber}", 234))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.numero_conta").value(234))
                .andExpect(jsonPath("$.saldo").value(170.07));

        verify(accountService, times(1)).getAccountByNumber(234);
    }

    @Test
    @DisplayName("Deve retornar 404 Not Found quando a conta não existir")
    void shouldReturnNotFoundWhenAccountDoesNotExist() throws Exception {
        when(accountService.getAccountByNumber(234)).thenReturn(Optional.empty());

        mockMvc.perform(get("/conta?numero_conta={accountNumber}", 234))
                .andExpect(status().isNotFound())
                .andExpect(content().string(""));

        verify(accountService, times(1)).getAccountByNumber(234);
    }


    @Test
    @DisplayName("Deve retornar 201 Created e JSON com detalhes da conta quando a conta for cadastrada com sucesso")
    void shouldReturnCreatedWhenAccountIsSuccessfullyCreated() throws Exception {
        when(accountService.createAccount(any())).thenReturn(accountDTO);

        mockMvc.perform(post("/conta")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(accountDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.numero_conta").value(234))
                .andExpect(jsonPath("$.saldo").value(170.07));

        verify(accountService, times(1)).createAccount(any());
    }


    @Test
    @DisplayName("Deve retornar 400 Bad Request quando a tentar cadastrar conta já existente")
    void shouldReturnBadRequestWhenAccountAlreadyExists() throws Exception {
        when(accountService.createAccount(any(Account.class)))
                .thenThrow(new AccountAlreadyExistsException("Conta de número 234 já existente."));

        mockMvc.perform(post("/conta")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(accountDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Conta de número 234 já existente."));
    }
}
