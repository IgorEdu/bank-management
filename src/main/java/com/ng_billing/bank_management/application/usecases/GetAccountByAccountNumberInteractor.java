package com.ng_billing.bank_management.application.usecases;

import com.ng_billing.bank_management.application.gateways.AccountGateway;
import com.ng_billing.bank_management.domain.entity.Account;

public class GetAccountByAccountNumberInteractor {
    private AccountGateway accountGateway;

    public GetAccountByAccountNumberInteractor(AccountGateway accountGateway){
        this.accountGateway = accountGateway;
    }

    public Account getAccountByAccountNumber(int accountNumber){
        return accountGateway.getAccountByAccountNumber(accountNumber);
    }
}
