package com.ng_billing.bank_management.infra.gateways;

import com.ng_billing.bank_management.domain.entity.Account;
import com.ng_billing.bank_management.infra.persistence.AccountEntity;

public class AccountEntityMapper {
    AccountEntity toEntity(Account accountDomainObject){
        return new AccountEntity(accountDomainObject.accountNumber(), accountDomainObject.balance());
    }

    Account toDomain(AccountEntity accountEntity){
        return new Account(accountEntity.getAccountNumber(), accountEntity.getBalance());
    }
}
