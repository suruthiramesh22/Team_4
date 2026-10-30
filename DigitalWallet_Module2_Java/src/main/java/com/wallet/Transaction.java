package com.wallet;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Transaction {
    private final String referenceId;
    private final long userId;
    private final TransactionType type;
    private final String bankAccountNumber;
    private final double amount;
    private final LocalDateTime timestamp;
    private final TransactionStatus status;
    private final String message;

    public Transaction(String referenceId, long userId, TransactionType type, String bankAccountNumber,
                       double amount, TransactionStatus status, String message) {
        this.referenceId = referenceId;
        this.userId = userId;
        this.type = type;
        this.bankAccountNumber = bankAccountNumber;
        this.amount = amount;
        this.timestamp = LocalDateTime.now();
        this.status = status;
        this.message = message;
    }

    public String getReferenceId() { return referenceId; }
    public long getUserId() { return userId; }
    public TransactionType getType() { return type; }
    public String getBankAccountNumber() { return bankAccountNumber; }
    public double getAmount() { return amount; }
    public LocalDateTime getTimestamp() { return timestamp; }
    public TransactionStatus getStatus() { return status; }
    public String getMessage() { return message; }

    @Override
    public String toString() {
        return String.format("%s | %s | Rs. %.2f | Bank: %s | %s | %s | %s",
                referenceId, type, amount, bankAccountNumber,
                timestamp.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")), status, message);
    }
}
