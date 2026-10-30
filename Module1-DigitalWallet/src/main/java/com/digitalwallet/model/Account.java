package com.digitalwallet.model;

import java.math.BigDecimal;

public abstract class Account {
    private final String accountId;
    private BigDecimal balance;

    protected Account(String accountId, BigDecimal openingBalance) {
        if (accountId == null || accountId.isBlank()) {
            throw new IllegalArgumentException("Account ID is required.");
        }
        if (openingBalance == null || openingBalance.signum() < 0) {
            throw new IllegalArgumentException("Opening balance cannot be negative.");
        }
        this.accountId = accountId;
        this.balance = openingBalance;
    }

    public String getAccountId() {
        return accountId;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void credit(BigDecimal amount) {
        validatePositiveAmount(amount);
        balance = balance.add(amount);
    }

    public void debit(BigDecimal amount) {
        validatePositiveAmount(amount);
        if (balance.compareTo(amount) < 0) {
            throw new IllegalStateException("Insufficient balance.");
        }
        balance = balance.subtract(amount);
    }

    private void validatePositiveAmount(BigDecimal amount) {
        if (amount == null || amount.signum() <= 0) {
            throw new IllegalArgumentException("Amount must be greater than zero.");
        }
    }

    public abstract String getAccountType();
}
