package com.wallet;

public class BankAccount {
    private final String accountNumber;
    private double balance;
    private boolean primary;
    private boolean hasPendingTransaction;

    public BankAccount(String accountNumber, double openingBalance, boolean primary) {
        if (accountNumber == null || accountNumber.isBlank()) throw new IllegalArgumentException("Account number is required.");
        if (openingBalance < 0) throw new IllegalArgumentException("Bank balance cannot be negative.");
        this.accountNumber = accountNumber;
        this.balance = openingBalance;
        this.primary = primary;
    }

    public String getAccountNumber() { return accountNumber; }
    public double getBalance() { return balance; }
    public boolean isPrimary() { return primary; }
    public boolean hasPendingTransaction() { return hasPendingTransaction; }

    public void setPrimary(boolean primary) { this.primary = primary; }
    public void setHasPendingTransaction(boolean value) { this.hasPendingTransaction = value; }

    public void debit(double amount) {
        if (amount <= 0) throw new IllegalArgumentException("Amount must be greater than zero.");
        if (balance < amount) throw new IllegalStateException("Insufficient bank balance.");
        balance -= amount;
    }

    public void credit(double amount) {
        if (amount <= 0) throw new IllegalArgumentException("Amount must be greater than zero.");
        balance += amount;
    }
}
