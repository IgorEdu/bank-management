package com.ng_billing.bank_management.infra.controllers;

import com.ng_billing.bank_management.application.usecases.GetAccountEntityByAccountNumberInteractor;
import com.ng_billing.bank_management.application.usecases.ProcessTransactionInteractor;
import com.ng_billing.bank_management.domain.entity.Transaction;
import com.ng_billing.bank_management.infra.exceptions.AccountNotFoundException;
import com.ng_billing.bank_management.infra.exceptions.InsufficientBalanceException;
import com.ng_billing.bank_management.infra.persistence.AccountEntity;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("/transacao")
public class TransactionController {
    private final ProcessTransactionInteractor processTransactionInteractor;
    private final GetAccountEntityByAccountNumberInteractor getAccountEntityByAccountNumberInteractor;

    public TransactionController(ProcessTransactionInteractor processTransactionInteractor, GetAccountEntityByAccountNumberInteractor getAccountEntityByAccountNumberInteractor) {
        this.processTransactionInteractor = processTransactionInteractor;
        this.getAccountEntityByAccountNumberInteractor = getAccountEntityByAccountNumberInteractor;
    }


    @PostMapping()
    public ResponseEntity<Object> createTransaction(@RequestBody @Valid TransactionDTO request){
        try {
            AccountEntity accountEntity = getAccountEntityByAccountNumberInteractor.getAccountEntityByAccountNumber(request.accountNumber());

            Transaction transaction = new Transaction(accountEntity, request.type(), request.amount());

            processTransactionInteractor.createTransaction(transaction);

            accountEntity =  getAccountEntityByAccountNumberInteractor.getAccountEntityByAccountNumber(request.accountNumber());
            AccountDTO response = new AccountDTO(accountEntity.getAccountNumber(), accountEntity.getBalance());

            URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                    .build()
                    .toUri();

            return ResponseEntity.created(location).body(response);
        } catch (InsufficientBalanceException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (AccountNotFoundException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }
}
