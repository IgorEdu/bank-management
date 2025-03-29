package com.ng_billing.bank_management.service;

import com.ng_billing.bank_management.domain.Account;
import com.ng_billing.bank_management.domain.AccountDTO;
import com.ng_billing.bank_management.exceptions.AccountAlreadyExistsException;
import com.ng_billing.bank_management.repository.AccountRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

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
        when(accountRepository.findByAccountNumber(234)).thenReturn(Optional.of(account));

        Optional<Account> result = accountService.getAccountByNumber(234);

        verify(accountRepository).findByAccountNumber(234);

        assertNotNull(result);
        assertEquals(234, result.get().getAccountNumber());
        assertEquals(170.07f, result.get().getBalance());
    }

    @Test
    void testGetAccountByNumber_ReturnsNull_WhenAccountDoesNotExist() {
        when(accountRepository.findByAccountNumber(234)).thenReturn(Optional.empty());

        Optional<Account> result = accountService.getAccountByNumber(234);

        assertFalse(result.isPresent());
    }

    @Test
    void testCreateAccount_ReturnsAccountDTO_WhenAccountIsCreated() {
        when(accountRepository.findByAccountNumber(234)).thenReturn(Optional.empty()); // No account found
        when(accountRepository.save(account)).thenReturn(account);

        AccountDTO result = accountService.createAccount(account);

        assertNotNull(result);
        assertEquals(234, result.accountNumber());
        assertEquals(170.07f, result.balance());
        verify(accountRepository).save(account);
    }

    @Test
    void testCreateAccount_ThrowsAccountAlreadyExistsException_WhenAccountAlreadyExists() {
        when(accountRepository.findByAccountNumber(234)).thenReturn(Optional.of(account)); // Account exists

        AccountAlreadyExistsException thrown = assertThrows(AccountAlreadyExistsException.class, () -> {
            accountService.createAccount(account);
        });

        assertEquals("Conta de número 234 já existente.", thrown.getMessage());
        verify(accountRepository, never()).save(account);
    }
}
