package com.ng_billing.bank_management.infra.controllers;

import com.ng_billing.bank_management.application.usecases.CreateAccountInteractor;
import com.ng_billing.bank_management.application.usecases.GetAccountByAccountNumberInteractor;
import com.ng_billing.bank_management.domain.entity.Account;
import com.ng_billing.bank_management.infra.exceptions.AccountAlreadyExistsException;
import com.ng_billing.bank_management.infra.exceptions.AccountNotFoundException;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.Optional;

@RestController
@RequestMapping("/conta")
public class AccountController {

    private final CreateAccountInteractor createAccountInteractor;
    private final GetAccountByAccountNumberInteractor getAccountByAccountNumberInteractor;

    public AccountController(CreateAccountInteractor createAccountInteractor, GetAccountByAccountNumberInteractor getAccountByAccountNumberInteractor) {
        this.createAccountInteractor = createAccountInteractor;
        this.getAccountByAccountNumberInteractor = getAccountByAccountNumberInteractor;
    }

    @GetMapping()
    public ResponseEntity<Object> getAccount(@RequestParam("numero_conta") int accountNumber){
        try {
            Account account = getAccountByAccountNumberInteractor.getAccountByAccountNumber(accountNumber);
            AccountDTO response = new AccountDTO(account.accountNumber(), account.balance());
            return ResponseEntity.ok(response);
        } catch (AccountNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @PostMapping()
    public ResponseEntity<Object> createAccount(@RequestBody @Valid AccountDTO request){
        try {
            Account account = new Account(request.accountNumber(), request.balance());
            Account accountCreated = createAccountInteractor.createAccount(account);
            AccountDTO response = new AccountDTO(accountCreated.accountNumber(), accountCreated.balance());

            URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                    .path("/{accountNumber}")
                    .buildAndExpand(response.accountNumber())
                    .toUri();

            return ResponseEntity.created(location).body(response);
        } catch (AccountAlreadyExistsException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

}
