package com.ng_billing.bank_management.service;

import com.ng_billing.bank_management.domain.Account;
import com.ng_billing.bank_management.domain.AccountDTO;
import com.ng_billing.bank_management.infra.exceptions.AccountAlreadyExistsException;
import com.ng_billing.bank_management.repository.AccountRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Optional;

@Service
public class AccountService {
    private final AccountRepository accountRepository;

    public AccountService(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    public Optional<Account> getAccountByNumber(int accountNumber) {
        return accountRepository.findByAccountNumber(accountNumber);
    }

    public AccountDTO createAccount(Account account){
        Optional<Account> existingAccount = accountRepository.findByAccountNumber(account.getAccountNumber());

        if (existingAccount.isPresent()) {
            throw new AccountAlreadyExistsException(account.getAccountNumber());
        }

        accountRepository.save(account);
        return new AccountDTO(account.getAccountNumber(), account.getBalance());
    }

    public void decreaseBalance(Account account, BigDecimal value){
        BigDecimal valueDecreased = account.getBalance().subtract(value);

        if (valueDecreased.compareTo(BigDecimal.ZERO) > 0) {
            account.setBalance(valueDecreased);

            accountRepository.save(account);
        }
    }
}
