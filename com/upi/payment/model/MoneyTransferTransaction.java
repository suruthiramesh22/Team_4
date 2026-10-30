package com.upi.payment.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Module 3: Money Transfer Transaction Model
 * Demonstrates:
 *  - Inheritance: Extends base abstract class Transaction.
 *  - Polymorphism: Overrides getTransactionSummary() method.
 *  - Encapsulation: Stores sender, receiver, amount, timestamp, status, and remarks with private fields.
 */
public class MoneyTransferTransaction extends Transaction {
    private final String senderPhone;
    private final String senderName;
    private final String receiverPhone;
    private final String receiverName;

    public MoneyTransferTransaction(String transactionId,
                                    String senderPhone,
                                    String senderName,
                                    String receiverPhone,
                                    String receiverName,
                                    BigDecimal amount,
                                    LocalDateTime timestamp,
                                    TransactionStatus status,
                                    String remarks) {
        super(transactionId, amount, timestamp, status, remarks);
        this.senderPhone = senderPhone;
        this.senderName = senderName;
        this.receiverPhone = receiverPhone;
        this.receiverName = receiverName;
    }

    public String getSenderPhone() { return senderPhone; }
    public String getSenderName() { return senderName; }
    public String getReceiverPhone() { return receiverPhone; }
    public String getReceiverName() { return receiverName; }

    /**
     * Demonstrates Polymorphism:
     * Overrides abstract method getTransactionSummary() with specific transfer details.
     */
    @Override
    public String getTransactionSummary() {
        return String.format("[%s] Ref: %-18s | Sender: %-8s (%s) -> Receiver: %-8s (%s) | Amount: ₹%-8.2f | Status: %-8s | Info: %s",
                getTimestamp().toLocalDate(),
                getTransactionId(),
                senderName, senderPhone,
                receiverName, receiverPhone,
                getAmount(),
                getStatus(),
                getRemarks());
    }
}
