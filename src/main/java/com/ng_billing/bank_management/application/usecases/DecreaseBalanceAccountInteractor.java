package com.ng_billing.bank_management.application.usecases;

import com.ng_billing.bank_management.application.gateways.AccountGateway;
import com.ng_billing.bank_management.infra.persistence.AccountEntity;

import java.math.BigDecimal;

public class DecreaseBalanceAccountInteractor {
    private AccountGateway accountGateway;

    public DecreaseBalanceAccountInteractor(AccountGateway accountGateway){
        this.accountGateway = accountGateway;
    }

    public AccountEntity decreaseBalance(AccountEntity account, BigDecimal amount){
        return accountGateway.decreaseAccountBalance(account, amount);
    }
}
