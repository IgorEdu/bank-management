package com.ng_billing.bank_management.service;

import com.ng_billing.bank_management.domain.Account;
import com.ng_billing.bank_management.repository.AccountRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AccountServiceTest {

    @InjectMocks
    private AccountService accountService;

    @Mock
    private AccountRepository accountRepository;

    private Account account;

    @BeforeEach
    void setUp() {
        account = new Account(234, 170.07f);
    }

    @Test
    void testGetAccountByNumber_ReturnsAccount() {
        when(accountRepository.findByAccountNumber(234)).thenReturn(account);

        Account result = accountService.getAccountByNumber(234);

        verify(accountRepository).findByAccountNumber(234);

        assertNotNull(result);
        assertEquals(234, result.getAccountNumber());
        assertEquals(170.07f, result.getBalance());
    }

    @Test
    void testGetAccountByNumber_ReturnsNull_WhenAccountDoesNotExist() {
        when(accountRepository.findByAccountNumber(234)).thenReturn(null);

        Account result = accountService.getAccountByNumber(234);

        assertNull(result);
    }
}
