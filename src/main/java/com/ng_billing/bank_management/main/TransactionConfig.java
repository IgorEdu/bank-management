package com.ng_billing.bank_management.main;

import com.ng_billing.bank_management.application.gateways.TransactionGateway;
import com.ng_billing.bank_management.application.usecases.DecreaseBalanceAccountInteractor;
import com.ng_billing.bank_management.application.usecases.ProcessTransactionInteractor;
import com.ng_billing.bank_management.domain.strategy.TransactionStrategyFactory;
import com.ng_billing.bank_management.infra.gateways.AccountEntityMapper;
import com.ng_billing.bank_management.infra.gateways.TransactionEntityMapper;
import com.ng_billing.bank_management.infra.gateways.TransactionRepositoryGateway;
import com.ng_billing.bank_management.infra.persistence.AccountRepository;
import com.ng_billing.bank_management.infra.persistence.TransactionRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TransactionConfig {
    @Bean
    ProcessTransactionInteractor createTransactionCase(TransactionGateway transactionGateway, TransactionStrategyFactory strategyFactory, AccountRepository accountRepository){
        return new ProcessTransactionInteractor(transactionGateway, strategyFactory, accountRepository);
    }


    @Bean
    TransactionGateway transactionGateway(TransactionRepository transactionRepository, TransactionEntityMapper transactionEntityMapper, DecreaseBalanceAccountInteractor decreaseBalanceAccountInteractor){
        return new TransactionRepositoryGateway(transactionRepository, transactionEntityMapper, decreaseBalanceAccountInteractor);
    }

    @Bean
    TransactionEntityMapper transactionEntityMapper(){
        return new TransactionEntityMapper(new AccountEntityMapper());
    }
}
