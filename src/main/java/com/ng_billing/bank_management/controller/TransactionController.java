package com.ng_billing.bank_management.controller;

import com.ng_billing.bank_management.domain.Account;
import com.ng_billing.bank_management.domain.AccountDTO;
import com.ng_billing.bank_management.domain.Transaction;
import com.ng_billing.bank_management.domain.TransactionDTO;
import com.ng_billing.bank_management.infra.exceptions.AccountNotFoundException;
import com.ng_billing.bank_management.infra.exceptions.InsufficientBalanceException;
import com.ng_billing.bank_management.service.AccountService;
import com.ng_billing.bank_management.service.TransactionService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.Optional;

@RestController
@RequestMapping("/transacao")
public class TransactionController {
    private final TransactionService transactionService;
    private final AccountService accountService;

    public TransactionController(TransactionService transactionService, AccountService accountService) {
        this.transactionService = transactionService;
        this.accountService = accountService;
    }

    @PostMapping()
    public ResponseEntity<Object> createTransaction(@RequestBody @Valid TransactionDTO request){
        try {
            Optional<Account> account = accountService.getAccountByNumber(request.accountNumber());

            if(account.isEmpty()){
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }

            Transaction transaction = new Transaction(account.get(), request.type(), request.amount());

            transactionService.processTransaction(transaction);

            account =  accountService.getAccountByNumber(request.accountNumber());
            AccountDTO response = new AccountDTO(account.get().getAccountNumber(), account.get().getBalance());

            URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                    .build()
                    .toUri();

            return ResponseEntity.created(location).body(response);
        } catch (InsufficientBalanceException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (AccountNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }
}
