package com.ng_billing.bank_management.application.usecases;

import com.ng_billing.bank_management.application.gateways.AccountGateway;
import com.ng_billing.bank_management.domain.entity.Account;
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
class CreateAccountInteractorTest {

    @InjectMocks
    private CreateAccountInteractor createAccountInteractor;

    @Mock
    private AccountGateway accountGateway;

    private Account account;

    @BeforeEach
    void setUp() {
        account = new Account(234, BigDecimal.valueOf(170.07));
    }

    @Test
    @DisplayName("Deve retornar a conta criada com sucesso")
    void shouldReturnCreatedAccountWhenCreateAccountIsCalled() {
        when(accountGateway.createAccount(account)).thenReturn(account);

        Account result = createAccountInteractor.createAccount(account);

        verify(accountGateway).createAccount(account);
        assertNotNull(result);
        assertEquals(234, result.accountNumber());
        assertEquals(BigDecimal.valueOf(170.07), result.balance());
    }
}
