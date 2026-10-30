package com.digitalwallet.model;

import java.math.BigDecimal;

public class BankAccount extends Account {
    private final String bankName;
    private final String accountNumber;
    private boolean primary;
    private int pendingTransactions;

    public BankAccount(String accountId, String bankName, String accountNumber,
                       BigDecimal openingBalance) {
        super(accountId, openingBalance);
        if (bankName == null || bankName.isBlank()) {
            throw new IllegalArgumentException("Bank name is required.");
        }
        if (accountNumber == null || !accountNumber.matches("[0-9]{6,20}")) {
            throw new IllegalArgumentException("Account number must contain 6 to 20 digits.");
        }
        this.bankName = bankName.trim();
        this.accountNumber = accountNumber;
        this.primary = false;
        this.pendingTransactions = 0;
    }

    public String getBankName() {
        return bankName;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public boolean isPrimary() {
        return primary;
    }

    public void setPrimary(boolean primary) {
        this.primary = primary;
    }

    public int getPendingTransactions() {
        return pendingTransactions;
    }

    public void addPendingTransaction() {
        pendingTransactions++;
    }

    public void completePendingTransaction() {
        if (pendingTransactions == 0) {
            throw new IllegalStateException("There are no pending transactions to complete.");
        }
        pendingTransactions--;
    }

    @Override
    public String getAccountType() {
        return "BANK_ACCOUNT";
    }
}
