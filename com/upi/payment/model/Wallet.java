package com.upi.payment.model;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Module 2: Wallet Management
 * Encapsulates the user's wallet balance using BigDecimal for precise financial calculations.
 * Provides thread-safe atomic debit and credit operations.
 */
public class Wallet {
    private final String walletId;
    private BigDecimal balance;

    public Wallet(String walletId, BigDecimal initialBalance) {
        this.walletId = walletId;
        this.balance = initialBalance != null ? initialBalance.setScale(2, RoundingMode.HALF_UP) : BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
    }

    public String getWalletId() {
        return walletId;
    }

    /**
     * Returns current balance (Encapsulation).
     */
    public synchronized BigDecimal getBalance() {
        return balance;
    }

    /**
     * Checks if the wallet has sufficient funds for the requested transfer.
     */
    public synchronized boolean hasSufficientBalance(BigDecimal amount) {
        return amount != null && amount.compareTo(BigDecimal.ZERO) > 0 && this.balance.compareTo(amount) >= 0;
    }

    /**
     * Atomically debits the specified amount from the wallet.
     * Prevents negative balance and partial updates.
     *
     * @param amount Amount to debit
     * @return true if debited successfully, false otherwise
     */
    public synchronized boolean debit(BigDecimal amount) {
        if (hasSufficientBalance(amount)) {
            this.balance = this.balance.subtract(amount);
            return true;
        }
        return false;
    }

    /**
     * Atomically credits the specified amount to the wallet.
     *
     * @param amount Amount to credit
     */
    public synchronized void credit(BigDecimal amount) {
        if (amount != null && amount.compareTo(BigDecimal.ZERO) > 0) {
            this.balance = this.balance.add(amount);
        }
    }
}
