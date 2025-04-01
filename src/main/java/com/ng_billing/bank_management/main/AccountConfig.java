package com.ng_billing.bank_management.main;

import com.ng_billing.bank_management.application.gateways.AccountGateway;
import com.ng_billing.bank_management.application.usecases.CreateAccountInteractor;
import com.ng_billing.bank_management.application.usecases.DecreaseBalanceAccountInteractor;
import com.ng_billing.bank_management.application.usecases.GetAccountByAccountNumberInteractor;
import com.ng_billing.bank_management.application.usecases.GetAccountEntityByAccountNumberInteractor;
import com.ng_billing.bank_management.infra.gateways.AccountEntityMapper;
import com.ng_billing.bank_management.infra.gateways.AccountRepositoryGateway;
import com.ng_billing.bank_management.infra.persistence.AccountRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AccountConfig {
    @Bean
    CreateAccountInteractor createAccountCase(AccountGateway accountGateway){
        return new CreateAccountInteractor(accountGateway);
    }

    @Bean
    GetAccountByAccountNumberInteractor getAccountByAccountNumberCase(AccountGateway accountGateway){
        return new GetAccountByAccountNumberInteractor(accountGateway);
    }

    @Bean
    GetAccountEntityByAccountNumberInteractor getAccountEntityByAccountNumberCase(AccountGateway accountGateway){
        return new GetAccountEntityByAccountNumberInteractor(accountGateway);
    }

    @Bean
    DecreaseBalanceAccountInteractor decreaseBalanceAccountInteractor(AccountGateway accountGateway){
        return new DecreaseBalanceAccountInteractor(accountGateway);
    }

    @Bean
    AccountGateway accountGateway(AccountRepository accountRepository, AccountEntityMapper accountEntityMapper){
        return new AccountRepositoryGateway(accountRepository, accountEntityMapper);
    }

    @Bean
    AccountEntityMapper accountEntityMapper(){
        return new AccountEntityMapper();
    }
}
