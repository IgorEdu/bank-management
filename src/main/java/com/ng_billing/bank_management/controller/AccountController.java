package com.ng_billing.bank_management.controller;

import com.ng_billing.bank_management.domain.Account;
import com.ng_billing.bank_management.domain.AccountDTO;
import com.ng_billing.bank_management.infra.exceptions.AccountAlreadyExistsException;
import com.ng_billing.bank_management.infra.exceptions.AccountNotFoundException;
import com.ng_billing.bank_management.service.AccountService;
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

    private final AccountService accountService;

    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    @GetMapping()
    public ResponseEntity<AccountDTO> getAccount(@RequestParam("numero_conta") int accountNumber){
        Optional<Account> account = accountService.getAccountByNumber(accountNumber);

        if(account.isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        AccountDTO response = new AccountDTO(account.get().getAccountNumber(), account.get().getBalance());
        return ResponseEntity.ok(response);
    }

    @PostMapping()
    public ResponseEntity<Object> createAccount(@RequestBody @Valid AccountDTO request){
        try {
            Account account = new Account(request.accountNumber(), request.balance());
            AccountDTO response = accountService.createAccount(account);

            URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                    .path("/{accountNumber}")
                    .buildAndExpand(response.accountNumber())
                    .toUri();

            return ResponseEntity.created(location).body(response);
        } catch (AccountAlreadyExistsException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (AccountNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

}
