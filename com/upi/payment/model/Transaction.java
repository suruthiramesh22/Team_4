package com.upi.payment.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Base Abstract Transaction Class
 * Demonstrates Abstraction (abstract method) and Encapsulation (private fields with accessors).
 * Base class for all transaction types in the digital payment system.
 */
public abstract class Transaction {
    private final String transactionId;
    private final BigDecimal amount;
    private final LocalDateTime timestamp;
    private TransactionStatus status;
    private String remarks;

    public Transaction(String transactionId, BigDecimal amount, LocalDateTime timestamp, TransactionStatus status, String remarks) {
        this.transactionId = transactionId;
        this.amount = amount;
        this.timestamp = timestamp;
        this.status = status;
        this.remarks = remarks;
    }

    public String getTransactionId() { return transactionId; }
    public BigDecimal getAmount() { return amount; }
    public LocalDateTime getTimestamp() { return timestamp; }
    public TransactionStatus getStatus() { return status; }
    public String getRemarks() { return remarks; }

    public void setStatus(TransactionStatus status) {
        this.status = status;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    /**
     * Demonstrates Abstraction:
     * Abstract method that must be implemented by subclasses to provide their own representation.
     */
    public abstract String getTransactionSummary();
}
