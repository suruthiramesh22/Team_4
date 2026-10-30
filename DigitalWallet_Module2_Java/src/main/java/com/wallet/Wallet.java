package com.wallet;

public class Wallet {
    private double balance;

    public double getBalance() { return balance; }

    public void debit(double amount) {
        if (amount <= 0) throw new IllegalArgumentException("Amount must be greater than zero.");
        if (balance < amount) throw new IllegalStateException("Insufficient wallet balance.");
        balance -= amount;
    }

    public void credit(double amount, double walletLimit) {
        if (amount <= 0) throw new IllegalArgumentException("Amount must be greater than zero.");
        if (balance + amount > walletLimit) {
            throw new IllegalStateException("Wallet limit exceeded. Allowed limit: Rs. " + walletLimit);
        }
        balance += amount;
    }
}
