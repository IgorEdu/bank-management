package com.ng_billing.bank_management.controller;

import com.ng_billing.bank_management.domain.Account;
import com.ng_billing.bank_management.domain.AccountResponseDTO;
import com.ng_billing.bank_management.service.AccountService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/conta")
public class AccountController {

    private final AccountService accountService;

    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    @GetMapping()
    public ResponseEntity getAccount(@RequestParam("numero_conta") int accountNumber){
        Account account = accountService.getAccountByNumber(accountNumber);

        if(account == null){
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Conta não encontrada");
        }

        AccountResponseDTO response = new AccountResponseDTO(account.getAccountNumber(), account.getBalance());
        return ResponseEntity.ok(response);
    }

}
