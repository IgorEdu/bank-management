package com.ng_billing.bank_management.infra.gateways;

import com.ng_billing.bank_management.application.gateways.TransactionGateway;
import com.ng_billing.bank_management.application.usecases.DecreaseBalanceAccountInteractor;
import com.ng_billing.bank_management.domain.entity.Transaction;
import com.ng_billing.bank_management.infra.persistence.TransactionEntity;
import com.ng_billing.bank_management.infra.persistence.TransactionRepository;

public class TransactionRepositoryGateway implements TransactionGateway {
    private final TransactionRepository transactionRepository;
    private final TransactionEntityMapper transactionEntityMapper;
    private final DecreaseBalanceAccountInteractor decreaseBalanceAccountInteractor;

    public TransactionRepositoryGateway(TransactionRepository transactionRepository, TransactionEntityMapper transactionEntityMapper, DecreaseBalanceAccountInteractor decreaseBalanceAccountInteractor) {
        this.transactionRepository = transactionRepository;
        this.transactionEntityMapper = transactionEntityMapper;
        this.decreaseBalanceAccountInteractor = decreaseBalanceAccountInteractor;
    }

    @Override
    public Transaction createTransaction(Transaction transaction) {
        TransactionEntity transactionEntity = transactionEntityMapper.toEntity(transaction);
        TransactionEntity transactionSaved = transactionRepository.save(transactionEntity);

        decreaseBalanceAccountInteractor.decreaseBalance(transaction.accountEntity(), transaction.amount());

        return transactionEntityMapper.toDomain(transactionSaved);
    }
}
