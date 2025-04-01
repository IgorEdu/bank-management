package com.ng_billing.bank_management.application.gateways;

import com.ng_billing.bank_management.domain.entity.Account;
import com.ng_billing.bank_management.infra.persistence.AccountEntity;

import java.math.BigDecimal;

public interface AccountGateway {
    Account createAccount(Account account);
    Account getAccountByAccountNumber(int accountNumber);
    AccountEntity getAccountEntityByAccountNumber(int accountNumber);
    AccountEntity decreaseAccountBalance(AccountEntity accountEntity, BigDecimal amount);
}
