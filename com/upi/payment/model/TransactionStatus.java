package com.upi.payment.model;

/**
 * Status enumeration for transactions.
 * Supports SUCCESS, FAILED, and REVERSED as required by Requirement 12.
 */
public enum TransactionStatus {
    SUCCESS,
    FAILED,
    REVERSED
}
