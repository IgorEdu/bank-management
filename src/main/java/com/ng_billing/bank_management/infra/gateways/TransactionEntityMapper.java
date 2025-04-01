package com.ng_billing.bank_management.infra.gateways;

import com.ng_billing.bank_management.domain.entity.Account;
import com.ng_billing.bank_management.domain.entity.Transaction;
import com.ng_billing.bank_management.infra.persistence.AccountEntity;
import com.ng_billing.bank_management.infra.persistence.TransactionEntity;

public class TransactionEntityMapper {
    private final AccountEntityMapper accountEntityMapper;

    public TransactionEntityMapper(AccountEntityMapper accountEntityMapper) {
        this.accountEntityMapper = accountEntityMapper;
    }


    TransactionEntity toEntity(Transaction transactionDomainObject){
        return new TransactionEntity(transactionDomainObject.accountEntity(), transactionDomainObject.type(), transactionDomainObject.amount());
    }

    Transaction toDomain(TransactionEntity transactionEntity){
        return new Transaction(transactionEntity.getAccount(), transactionEntity.getType(), transactionEntity.getAmount());
    }
}
