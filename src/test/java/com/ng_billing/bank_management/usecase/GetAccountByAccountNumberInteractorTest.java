package com.ng_billing.bank_management.usecase;

import com.ng_billing.bank_management.application.gateways.AccountGateway;
import com.ng_billing.bank_management.application.usecases.GetAccountByAccountNumberInteractor;
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
class GetAccountByAccountNumberInteractorTest {

    @InjectMocks
    private GetAccountByAccountNumberInteractor getAccountByAccountNumberInteractor;

    @Mock
    private AccountGateway accountGateway;

    private Account account;

    @BeforeEach
    void setUp() {
        account = new Account(234, BigDecimal.valueOf(170.07));
    }

    @Test
    @DisplayName("Deve retornar a conta quando a conta com o número fornecido existir")
    void shouldReturnAccountWhenAccountExists() {
        when(accountGateway.getAccountByAccountNumber(234)).thenReturn(account);

        Account result = getAccountByAccountNumberInteractor.getAccountByAccountNumber(234);

        verify(accountGateway).getAccountByAccountNumber(234);
        assertNotNull(result);
        assertEquals(234, result.accountNumber());
        assertEquals(BigDecimal.valueOf(170.07), result.balance());
    }
}
