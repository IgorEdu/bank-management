package com.ng_billing.bank_management.infra.gateways;

import com.ng_billing.bank_management.application.gateways.AccountGateway;
import com.ng_billing.bank_management.domain.entity.Account;
import com.ng_billing.bank_management.infra.exceptions.AccountAlreadyExistsException;
import com.ng_billing.bank_management.infra.exceptions.AccountNotFoundException;
import com.ng_billing.bank_management.infra.persistence.AccountEntity;
import com.ng_billing.bank_management.infra.persistence.AccountRepository;

import java.math.BigDecimal;
import java.util.Optional;

public class AccountRepositoryGateway implements AccountGateway {
    private final AccountRepository accountRepository;
    private final AccountEntityMapper accountEntityMapper;

    public AccountRepositoryGateway(AccountRepository accountRepository, AccountEntityMapper accountEntityMapper) {
        this.accountRepository = accountRepository;
        this.accountEntityMapper = accountEntityMapper;
    }

    @Override
    public Account createAccount(Account account) {
        if(accountRepository.findByAccountNumber(account.accountNumber()) != null){
            throw new AccountAlreadyExistsException(account.accountNumber());
        }

        AccountEntity accountEntity = accountEntityMapper.toEntity(account);
        AccountEntity accountSaved = accountRepository.save(accountEntity);
        return accountEntityMapper.toDomain(accountSaved);
    }

    @Override
    public Account getAccountByAccountNumber(int accountNumber) {
        AccountEntity account = accountRepository.findByAccountNumber(accountNumber);

        if (account == null) {
            throw new AccountNotFoundException(accountNumber);
        }

        return accountEntityMapper.toDomain(account);
    }

    public AccountEntity getAccountEntityByAccountNumber(int accountNumber) {
        AccountEntity accountEntity = accountRepository.findByAccountNumber(accountNumber);

        if (accountEntity == null) {
            throw new AccountNotFoundException(accountNumber);
        }

        return accountEntity;
    }

    @Override
    public AccountEntity decreaseAccountBalance(AccountEntity accountEntity, BigDecimal amount) {
        BigDecimal decreasedBalance = accountEntity.getBalance().subtract(amount);

        accountEntity.setBalance(decreasedBalance);
        return accountRepository.save(accountEntity);
    }
}
