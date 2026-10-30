package CashbackModule;

import java.time.LocalDateTime;

/**
 * Records a cashback credit or reversal.
 * This is an in-memory model; no database is required.
 */
public class CashbackTransaction {
    public enum Type {
        CREDIT,
        REVERSAL
    }

    private final String referenceId;
    private final String userId;
    private final double amount;
    private final Type type;
    private final LocalDateTime createdAt;
    private boolean reversed;

    public CashbackTransaction(String referenceId, String userId, double amount, Type type) {
        if (referenceId == null || referenceId.isBlank()) {
            throw new IllegalArgumentException("Reference ID is required.");
        }
        if (userId == null || userId.isBlank()) {
            throw new IllegalArgumentException("User ID is required.");
        }
        if (amount < 0) {
            throw new IllegalArgumentException("Amount cannot be negative.");
        }
        this.referenceId = referenceId;
        this.userId = userId;
        this.amount = amount;
        this.type = type;
        this.createdAt = LocalDateTime.now();
        this.reversed = false;
    }

    public String getReferenceId() { return referenceId; }
    public String getUserId() { return userId; }
    public double getAmount() { return amount; }
    public Type getType() { return type; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public boolean isReversed() { return reversed; }
    public void markReversed() { this.reversed = true; }
}
