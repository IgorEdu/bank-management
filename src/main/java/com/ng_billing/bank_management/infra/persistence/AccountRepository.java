package com.ng_billing.bank_management.infra.persistence;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface AccountRepository extends JpaRepository<AccountEntity, UUID> {

    AccountEntity findByAccountNumber(int accountNumber);
}
