package com.ng_billing.bank_management.controller;

import com.ng_billing.bank_management.domain.Account;
import com.ng_billing.bank_management.domain.AccountResponseDTO;
import com.ng_billing.bank_management.service.AccountService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class AccountControllerTest {

    @InjectMocks
    private AccountController accountController;

    @Mock
    private AccountService accountService;

    private Account account;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        account = new Account(234, 170.07f);
    }

    @Test
    void testGetAccount_ReturnsAccount() {
        when(accountService.getAccountByNumber(234)).thenReturn(account);

        ResponseEntity<AccountResponseDTO> response = accountController.getAccount(234);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(234, response.getBody().accountNumber());
        assertEquals(170.07f, response.getBody().balance());
    }

    @Test
    void testGetAccount_ReturnsNotFound_WhenAccountDoesNotExist() {
        when(accountService.getAccountByNumber(234)).thenReturn(null);

        ResponseEntity<AccountResponseDTO> response = accountController.getAccount(234);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());
    }
}
