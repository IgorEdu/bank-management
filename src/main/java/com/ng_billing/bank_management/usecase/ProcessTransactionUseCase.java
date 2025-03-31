package com.ng_billing.bank_management.usecase;

import com.ng_billing.bank_management.domain.Account;
import com.ng_billing.bank_management.domain.Transaction;
import com.ng_billing.bank_management.infra.exceptions.InsufficientBalanceException;
import com.ng_billing.bank_management.service.AccountService;
import com.ng_billing.bank_management.strategy.TransactionStrategyFactory;
import org.springframework.stereotype.Component;

import javax.security.auth.login.AccountNotFoundException;
import java.math.BigDecimal;

@Component
public class ProcessTransactionUseCase {

    private final AccountService accountService;
    private final TransactionStrategyFactory strategyFactory;

    public ProcessTransactionUseCase(AccountService accountService, TransactionStrategyFactory strategyFactory) {
        this.accountService = accountService;
        this.strategyFactory = strategyFactory;
    }

    public void execute(Transaction transaction) throws AccountNotFoundException {
        Account account = accountService.getAccountByNumber(transaction.getAccount().getAccountNumber())
                .orElseThrow(() -> new AccountNotFoundException("Conta não encontrada"));

        BigDecimal totalAmount = strategyFactory.getStrategy(transaction.getType())
                .calculateTotalAmount(transaction.getAmount());

        if (totalAmount.compareTo(account.getBalance()) > 0) {
            throw new InsufficientBalanceException();
        }

        accountService.decreaseBalance(account, totalAmount);
    }
}
