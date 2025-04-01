package com.ng_billing.bank_management.usecase;

import com.ng_billing.bank_management.application.gateways.AccountGateway;
import com.ng_billing.bank_management.application.usecases.DecreaseBalanceAccountInteractor;
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
class DecreaseBalanceAccountInteractorTest {

    @InjectMocks
    private DecreaseBalanceAccountInteractor decreaseBalanceAccountInteractor;

    @Mock
    private AccountGateway accountGateway;

    private AccountEntity accountEntity;

    @BeforeEach
    void setUp() {
        accountEntity = new AccountEntity(234, BigDecimal.valueOf(170.07));
    }

    @Test
    @DisplayName("Deve diminuir o saldo da conta com sucesso")
    void shouldDecreaseBalanceWhenAmountIsValid() {
        BigDecimal amountToDecrease = BigDecimal.valueOf(50);
        AccountEntity expectedAccountEntity =  new AccountEntity(234, BigDecimal.valueOf(120.07));


        when(accountGateway.decreaseAccountBalance(accountEntity, amountToDecrease)).thenReturn(expectedAccountEntity);

        AccountEntity result = decreaseBalanceAccountInteractor.decreaseBalance(accountEntity, amountToDecrease);

        verify(accountGateway).decreaseAccountBalance(accountEntity, amountToDecrease);
        assertNotNull(result);
        assertSame(expectedAccountEntity, result);
        assertEquals(234, result.getAccountNumber());
        assertEquals(BigDecimal.valueOf(120.07), result.getBalance());
    }
}
