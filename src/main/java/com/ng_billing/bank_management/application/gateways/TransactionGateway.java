package com.ng_billing.bank_management.application.gateways;

import com.ng_billing.bank_management.domain.entity.Transaction;

public interface TransactionGateway {
    Transaction createTransaction(Transaction transaction);
}
