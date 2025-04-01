package com.ng_billing.bank_management.application.usecases;

import com.ng_billing.bank_management.application.gateways.AccountGateway;
import com.ng_billing.bank_management.infra.exceptions.AccountNotFoundException;
import com.ng_billing.bank_management.infra.persistence.AccountEntity;

public class GetAccountEntityByAccountNumberInteractor {
    private AccountGateway accountGateway;

    public GetAccountEntityByAccountNumberInteractor(AccountGateway accountGateway){
        this.accountGateway = accountGateway;
    }

    public AccountEntity getAccountEntityByAccountNumber(int accountNumber){
        AccountEntity account = accountGateway.getAccountEntityByAccountNumber(accountNumber);

        if(account == null){
            throw new AccountNotFoundException(accountNumber);
        }

        return account;
    }
}
