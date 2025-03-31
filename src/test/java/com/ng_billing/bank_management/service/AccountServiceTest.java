package com.ng_billing.bank_management.service;

import com.ng_billing.bank_management.domain.Account;
import com.ng_billing.bank_management.domain.AccountDTO;
import com.ng_billing.bank_management.infra.exceptions.AccountAlreadyExistsException;
import com.ng_billing.bank_management.repository.AccountRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
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
        account = new Account(234, BigDecimal.valueOf(170.07));
    }

    @Test
    @DisplayName("Deve retornar a conta quando a conta com o número fornecido existir")
    void shouldReturnAccountWhenAccountExists() {
        when(accountRepository.findByAccountNumber(234)).thenReturn(Optional.of(account));

        Optional<Account> result = accountService.getAccountByNumber(234);

        verify(accountRepository).findByAccountNumber(234);

        assertNotNull(result);
        assertEquals(234, result.get().getAccountNumber());
        assertEquals(BigDecimal.valueOf(170.07), result.get().getBalance());
    }

    @Test
    @DisplayName("Deve retornar Optional vazio quando a conta com o número fornecido não existir")
    void shouldReturnEmptyWhenAccountDoesNotExist() {
        when(accountRepository.findByAccountNumber(234)).thenReturn(Optional.empty());

        Optional<Account> result = accountService.getAccountByNumber(234);

        assertFalse(result.isPresent());
    }

    @Test
    @DisplayName("Deve retornar um AccountDTO quando a conta for criada com sucesso")
    void shouldReturnAccountDTOWhenAccountIsCreatedSuccessfully() {
        when(accountRepository.findByAccountNumber(234)).thenReturn(Optional.empty()); // No account found
        when(accountRepository.save(account)).thenReturn(account);

        AccountDTO result = accountService.createAccount(account);

        assertNotNull(result);
        assertEquals(234, result.accountNumber());
        assertEquals(BigDecimal.valueOf(170.07), result.balance());
        verify(accountRepository).save(account);
    }

    @Test
    @DisplayName("Deve lançar AccountAlreadyExistsException quando a conta já existir")
    void shouldThrowAccountAlreadyExistsExceptionWhenAccountAlreadyExists() {
        when(accountRepository.findByAccountNumber(234)).thenReturn(Optional.of(account)); // Account exists

        AccountAlreadyExistsException thrown = assertThrows(AccountAlreadyExistsException.class, () -> {
            accountService.createAccount(account);
        });

        assertEquals("Conta de número 234 já existente.", thrown.getMessage());
        verify(accountRepository, never()).save(account);
    }
}
