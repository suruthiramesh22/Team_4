package com.upi.payment.service;

import com.upi.payment.model.MoneyTransferTransaction;

/**
 * Result wrapper for Money Transfer operations.
 * Encapsulates success status, user-friendly message, and the transaction reference.
 */
public class TransferResult {
    private final boolean success;
    private final String message;
    private final MoneyTransferTransaction transaction;

    public TransferResult(boolean success, String message, MoneyTransferTransaction transaction) {
        this.success = success;
        this.message = message;
        this.transaction = transaction;
    }

    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }

    public MoneyTransferTransaction getTransaction() {
        return transaction;
    }

    @Override
    public String toString() {
        return (success ? "[SUCCESS] " : "[FAILED] ") + message +
                (transaction != null ? " (Txn ID: " + transaction.getTransactionId() + ")" : "");
    }
}
