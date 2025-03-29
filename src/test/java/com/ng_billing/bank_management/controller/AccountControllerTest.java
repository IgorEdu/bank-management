package com.ng_billing.bank_management.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ng_billing.bank_management.domain.Account;
import com.ng_billing.bank_management.domain.AccountDTO;
import com.ng_billing.bank_management.exceptions.AccountAlreadyExistsException;
import com.ng_billing.bank_management.service.AccountService;
import org.junit.jupiter.api.BeforeEach;
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

    private Account account;
    private AccountDTO accountDTO;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        account = new Account(234, 170.07f);
        accountDTO = new AccountDTO(account.getAccountNumber(), account.getBalance());
    }

    @Test
    void testGetAccount_ReturnsAccount() throws Exception {
        when(accountService.getAccountByNumber(234)).thenReturn(Optional.of(account));

        mockMvc.perform(get("/conta?numero_conta={accountNumber}", 234))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accountNumber").value(234))
                .andExpect(jsonPath("$.balance").value(170.07));

        verify(accountService, times(1)).getAccountByNumber(234);
    }

    @Test
    void testGetAccount_ReturnsNotFound_WhenAccountDoesNotExist() throws Exception {
        when(accountService.getAccountByNumber(234)).thenReturn(Optional.empty());

        mockMvc.perform(get("/conta?numero_conta={accountNumber}", 234))
                .andExpect(status().isNotFound())
                .andExpect(content().string(""));

        verify(accountService, times(1)).getAccountByNumber(234);
    }


    @Test
    void testCreateAccount_ReturnsCreated() throws Exception {
        when(accountService.createAccount(any())).thenReturn(accountDTO);

        mockMvc.perform(post("/conta")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(accountDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.accountNumber").value(234))
                .andExpect(jsonPath("$.balance").value(170.07));

        verify(accountService, times(1)).createAccount(any());
    }


    @Test
    void testCreateAccount_ReturnsBadRequest_WhenAccountAlreadyExists() throws Exception {
        when(accountService.createAccount(any(Account.class)))
                .thenThrow(new AccountAlreadyExistsException("Conta de número 234 já existente."));

        mockMvc.perform(post("/conta")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(accountDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Conta de número 234 já existente."));
    }
}
