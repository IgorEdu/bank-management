package com.ng_billing.bank_management.repository;

import com.ng_billing.bank_management.domain.Account;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface AccountRepository extends JpaRepository<Account, UUID> {

    Optional<Account> findByAccountNumber(int accountNumber);
}
