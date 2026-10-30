package com.upi.payment.service;

import java.math.BigDecimal;

/**
 * Module 3 Service Interface: Money Transfer
 * Demonstrates Abstraction by declaring high-level business capabilities
 * without exposing internal implementation details.
 */
public interface MoneyTransferService {

    /**
     * Transfers money between two registered users using phone numbers.
     *
     * @param senderPhone   10-digit registered phone number of sender
     * @param receiverPhone 10-digit registered phone number of receiver
     * @param amount        Amount to transfer (using BigDecimal)
     * @param pin           4-digit sender UPI PIN
     * @return TransferResult containing outcome status, feedback message, and transaction details
     */
    TransferResult transferMoney(String senderPhone, String receiverPhone, BigDecimal amount, String pin);

    /**
     * Reverses an earlier successful money transfer atomically.
     *
     * @param transactionId Reference ID of the transaction to reverse
     * @param reason        Reason for reversal
     * @return true if reversed successfully, false otherwise
     */
    boolean reverseTransaction(String transactionId, String reason);

    /**
     * Calculates total successful money transfers sent by the user today.
     *
     * @param senderPhone Sender phone number
     * @return Total amount sent today as BigDecimal
     */
    BigDecimal getDailySuccessfulTransferTotal(String senderPhone);
}
