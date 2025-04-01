package com.ng_billing.bank_management.usecase;

import com.ng_billing.bank_management.application.gateways.AccountGateway;
import com.ng_billing.bank_management.application.usecases.GetAccountEntityByAccountNumberInteractor;
import com.ng_billing.bank_management.infra.exceptions.AccountNotFoundException;
import com.ng_billing.bank_management.infra.persistence.AccountEntity;
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
class GetAccountEntityByAccountNumberInteractorTest {

    @InjectMocks
    private GetAccountEntityByAccountNumberInteractor getAccountEntityByAccountNumberInteractor;

    @Mock
    private AccountGateway accountGateway;

    private AccountEntity accountEntity;

    @BeforeEach
    void setUp() {
        accountEntity = new AccountEntity(234, BigDecimal.valueOf(170.07));
    }

    @Test
    @DisplayName("Deve lançar AccountNotFoundException quando a conta não existir")
    void shouldThrowAccountNotFoundExceptionWhenAccountDoesNotExist() {
        when(accountGateway.getAccountEntityByAccountNumber(234)).thenReturn(null);

        AccountNotFoundException thrown = assertThrows(AccountNotFoundException.class, () -> {
            getAccountEntityByAccountNumberInteractor.getAccountEntityByAccountNumber(234);
        });

        assertEquals("Conta não encontrada para o número: 234", thrown.getMessage());
    }

    @Test
    @DisplayName("Deve retornar a conta quando a conta com o número fornecido existir")
    void shouldReturnAccountEntityWhenAccountExists() {
        when(accountGateway.getAccountEntityByAccountNumber(234)).thenReturn(accountEntity);

        AccountEntity result = getAccountEntityByAccountNumberInteractor.getAccountEntityByAccountNumber(234);

        verify(accountGateway).getAccountEntityByAccountNumber(234);
        assertNotNull(result);
        assertEquals(234, result.getAccountNumber());
        assertEquals(BigDecimal.valueOf(170.07), result.getBalance());
    }
}
