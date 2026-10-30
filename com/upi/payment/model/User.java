package com.upi.payment.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Module 1: User Profile & Security Management
 * Encapsulates user profile details, UPI PIN security, failed attempt counters,
 * and automatic 24-hour lockout enforcement.
 */
public class User {
    private final String userId;
    private final String fullName;
    private final String phoneNumber;
    private final String email;
    private String upiPin; // 4-digit secret UPI PIN
    private UserStatus status;
    private final Wallet wallet;

    // PIN attempt security tracking (Requirement 7)
    private int failedPinAttempts;
    private LocalDateTime lockedUntil;

    public User(String userId, String fullName, String phoneNumber, String email, String upiPin, BigDecimal initialBalance) {
        this.userId = userId;
        this.fullName = fullName;
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.upiPin = upiPin;
        this.status = UserStatus.ACTIVE;
        this.wallet = new Wallet("WAL-" + userId, initialBalance);
        this.failedPinAttempts = 0;
        this.lockedUntil = null;
    }

    public String getUserId() { return userId; }
    public String getFullName() { return fullName; }
    public String getPhoneNumber() { return phoneNumber; }
    public String getEmail() { return email; }
    public UserStatus getStatus() { return status; }
    public Wallet getWallet() { return wallet; }
    public int getFailedPinAttempts() { return failedPinAttempts; }
    public LocalDateTime getLockedUntil() { return lockedUntil; }

    public void setStatus(UserStatus status) {
        this.status = status;
    }

    public void setUpiPin(String upiPin) {
        this.upiPin = upiPin;
    }

    /**
     * Checks if transactions are locked due to 3 consecutive failed PIN attempts.
     * Automatically unlocks once the 24-hour lockout window has expired.
     */
    public synchronized boolean isPinLocked() {
        if (lockedUntil == null) {
            return false;
        }
        if (LocalDateTime.now().isBefore(lockedUntil)) {
            return true;
        }
        // 24 hours have elapsed: reset lockout state
        lockedUntil = null;
        failedPinAttempts = 0;
        return false;
    }

    /**
     * Verifies the user's 4-digit PIN.
     * Enforces Requirement 7: Lock transactions for 24 hours after 3 incorrect attempts.
     *
     * @param inputPin 4-digit PIN entered by user
     * @return true if valid and not locked; false otherwise
     */
    public synchronized boolean verifyPin(String inputPin) {
        // If already locked, reject attempt immediately
        if (isPinLocked()) {
            return false;
        }

        if (this.upiPin != null && this.upiPin.equals(inputPin)) {
            // Correct PIN resets failure counter
            this.failedPinAttempts = 0;
            this.lockedUntil = null;
            return true;
        } else {
            // Increment failure count
            this.failedPinAttempts++;
            if (this.failedPinAttempts >= 3) {
                // Lock transactions for 24 hours from current timestamp
                this.lockedUntil = LocalDateTime.now().plusHours(24);
            }
            return false;
        }
    }

    /**
     * Manual unlock/reset utility (e.g. for customer support or unit testing).
     */
    public synchronized void resetPinLock() {
        this.failedPinAttempts = 0;
        this.lockedUntil = null;
    }
}
