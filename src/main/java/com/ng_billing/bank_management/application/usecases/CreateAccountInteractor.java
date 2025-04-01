package com.ng_billing.bank_management.application.usecases;

import com.ng_billing.bank_management.application.gateways.AccountGateway;
import com.ng_billing.bank_management.domain.entity.Account;

public class CreateAccountInteractor {
    private AccountGateway accountGateway;

    public CreateAccountInteractor(AccountGateway accountGateway){
        this.accountGateway = accountGateway;
    }

    public Account createAccount(Account account){
        return accountGateway.createAccount(account);
    }
}
