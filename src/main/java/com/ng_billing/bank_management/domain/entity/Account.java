package com.ng_billing.bank_management.domain.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.util.UUID;

public record Account(int accountNumber, BigDecimal balance) {
}
