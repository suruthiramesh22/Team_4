package com.upi.payment.service;

import com.upi.payment.model.*;
import com.upi.payment.repository.InMemoryDataStore;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Random;

/**
 * Module 3 Core Implementation: Money Transfer Service
 * Implements all 15 project requirements:
 *  1. Transfer using receiver phone number
 *  2. Validate receiver exists and status is ACTIVE
 *  3. Prevent self-transfer
 *  4. Enforce ₹1.00 min and ₹10,000.00 max per transaction
 *  5. Daily transfer limit cap of ₹25,000.00 per sender
 *  6. Verify sender's 4-digit UPI PIN
 *  7. Lockout for 24 hours after 3 consecutive wrong PIN attempts
 *  8. Verify sufficient sender wallet balance
 *  9. Atomic debit & credit without partial updates
 * 10. Generate unique UPI reference ID
 * 11. Store sender, receiver, amount, timestamp, status
 * 12. Support SUCCESS, FAILED, and REVERSED statuses
 * 13. Dynamic in-memory collection storage
 * 14. Demonstrates Encapsulation, Inheritance, Abstraction, Polymorphism
 * 15. Integrates with existing models and store
 */
public class MoneyTransferServiceImpl implements MoneyTransferService {

    // Transaction Limit Constants as per requirements
    public static final BigDecimal MIN_TRANSACTION_AMOUNT = new BigDecimal("1.00").setScale(2, RoundingMode.HALF_UP);
    public static final BigDecimal MAX_TRANSACTION_AMOUNT = new BigDecimal("10000.00").setScale(2, RoundingMode.HALF_UP);
    public static final BigDecimal DAILY_SUCCESSFUL_LIMIT = new BigDecimal("25000.00").setScale(2, RoundingMode.HALF_UP);
    public static final int MAX_PIN_ATTEMPTS = 3;

    private final InMemoryDataStore dataStore;
    private final Random random = new Random();

    public MoneyTransferServiceImpl() {
        this.dataStore = InMemoryDataStore.getInstance();
    }

    public MoneyTransferServiceImpl(InMemoryDataStore dataStore) {
        this.dataStore = dataStore;
    }

    @Override
    public TransferResult transferMoney(String senderPhone, String receiverPhone, BigDecimal amount, String pin) {
        // Normalize amount to 2 decimal places
        BigDecimal transferAmount = (amount != null) ? amount.setScale(2, RoundingMode.HALF_UP) : BigDecimal.ZERO;
        String txnId = generateReferenceId();
        LocalDateTime now = LocalDateTime.now();

        // 1. Basic Parameter Validations
        if (senderPhone == null || senderPhone.trim().isEmpty()) {
            return recordAndReturnFailure(txnId, "", "Unknown", receiverPhone, "Unknown",
                    transferAmount, now, "Sender phone number cannot be empty.");
        }
        if (receiverPhone == null || receiverPhone.trim().isEmpty()) {
            return recordAndReturnFailure(txnId, senderPhone, "Unknown", "", "Unknown",
                    transferAmount, now, "Receiver phone number cannot be empty.");
        }

        senderPhone = senderPhone.trim();
        receiverPhone = receiverPhone.trim();

        // 2. Prevent Self-Transfer (Requirement 3)
        if (senderPhone.equalsIgnoreCase(receiverPhone)) {
            return recordAndReturnFailure(txnId, senderPhone, "Self", receiverPhone, "Self",
                    transferAmount, now, "Self-transfer is not permitted.");
        }

        // 3. Amount Bounds Validation (Requirement 4: Min ₹1, Max ₹10,000)
        if (transferAmount.compareTo(MIN_TRANSACTION_AMOUNT) < 0) {
            return recordAndReturnFailure(txnId, senderPhone, "Sender", receiverPhone, "Receiver",
                    transferAmount, now, String.format("Minimum transfer amount is ₹%.2f.", MIN_TRANSACTION_AMOUNT));
        }
        if (transferAmount.compareTo(MAX_TRANSACTION_AMOUNT) > 0) {
            return recordAndReturnFailure(txnId, senderPhone, "Sender", receiverPhone, "Receiver",
                    transferAmount, now, String.format("Maximum transfer amount per transaction is ₹%.2f.", MAX_TRANSACTION_AMOUNT));
        }

        // 4. Verify Sender Existence & Active Status
        Optional<User> senderOpt = dataStore.findUserByPhone(senderPhone);
        if (senderOpt.isEmpty()) {
            return recordAndReturnFailure(txnId, senderPhone, "Unknown", receiverPhone, "Unknown",
                    transferAmount, now, "Sender is not registered in the system.");
        }
        User sender = senderOpt.get();
        if (sender.getStatus() != UserStatus.ACTIVE) {
            return recordAndReturnFailure(txnId, senderPhone, sender.getFullName(), receiverPhone, "Unknown",
                    transferAmount, now, "Sender account is " + sender.getStatus() + ". Transactions disallowed.");
        }

        // 5. Verify Receiver Existence & Active Status (Requirement 1 & 2)
        Optional<User> receiverOpt = dataStore.findUserByPhone(receiverPhone);
        if (receiverOpt.isEmpty()) {
            return recordAndReturnFailure(txnId, senderPhone, sender.getFullName(), receiverPhone, "Unknown",
                    transferAmount, now, "Receiver with phone " + receiverPhone + " does not exist.");
        }
        User receiver = receiverOpt.get();
        if (receiver.getStatus() != UserStatus.ACTIVE) {
            return recordAndReturnFailure(txnId, senderPhone, sender.getFullName(), receiverPhone, receiver.getFullName(),
                    transferAmount, now, "Receiver account is " + receiver.getStatus() + ". Transfer cannot be completed.");
        }

        // 6. Check 24-Hour PIN Lockout (Requirement 7)
        if (sender.isPinLocked()) {
            return recordAndReturnFailure(txnId, senderPhone, sender.getFullName(), receiverPhone, receiver.getFullName(),
                    transferAmount, now, "Account is locked for 24 hours due to 3 incorrect PIN attempts. Try after: " + sender.getLockedUntil());
        }

        // 7. Verify Sender 4-Digit PIN (Requirement 6 & 7)
        if (pin == null || !pin.matches("^\\d{4}$")) {
            // Count malformed format as invalid attempt
            sender.verifyPin("INVALID");
            int remaining = MAX_PIN_ATTEMPTS - sender.getFailedPinAttempts();
            String msg = sender.isPinLocked()
                    ? "Invalid PIN format. 3 consecutive failed attempts! Account locked for 24 hours."
                    : "Invalid PIN format (must be 4 digits). Attempts remaining: " + Math.max(0, remaining);
            return recordAndReturnFailure(txnId, senderPhone, sender.getFullName(), receiverPhone, receiver.getFullName(),
                    transferAmount, now, msg);
        }

        boolean pinValid = sender.verifyPin(pin);
        if (!pinValid) {
            int remaining = MAX_PIN_ATTEMPTS - sender.getFailedPinAttempts();
            String msg = sender.isPinLocked()
                    ? "Incorrect 4-digit PIN! 3 consecutive failed attempts reached. Account locked for 24 hours."
                    : "Incorrect 4-digit PIN. Attempts remaining: " + Math.max(0, remaining);
            return recordAndReturnFailure(txnId, senderPhone, sender.getFullName(), receiverPhone, receiver.getFullName(),
                    transferAmount, now, msg);
        }

        // 8. Daily Successful Transfer Limit Check (Requirement 5: Max ₹25,000 per sender per day)
        BigDecimal currentDailyTotal = getDailySuccessfulTransferTotal(senderPhone);
        BigDecimal projectedDailyTotal = currentDailyTotal.add(transferAmount);
        if (projectedDailyTotal.compareTo(DAILY_SUCCESSFUL_LIMIT) > 0) {
            return recordAndReturnFailure(txnId, senderPhone, sender.getFullName(), receiverPhone, receiver.getFullName(),
                    transferAmount, now, String.format("Daily transfer limit of ₹%.2f exceeded. Already transferred today: ₹%.2f, Requested: ₹%.2f.",
                            DAILY_SUCCESSFUL_LIMIT, currentDailyTotal, transferAmount));
        }

        // 9. Check Sender Sufficient Wallet Balance (Requirement 8)
        if (!sender.getWallet().hasSufficientBalance(transferAmount)) {
            return recordAndReturnFailure(txnId, senderPhone, sender.getFullName(), receiverPhone, receiver.getFullName(),
                    transferAmount, now, String.format("Insufficient wallet balance. Available: ₹%.2f, Required: ₹%.2f.",
                            sender.getWallet().getBalance(), transferAmount));
        }

        // 10. Atomic Transfer Execution (Requirement 9: Debit sender and credit receiver atomically)
        // Prevent deadlocks during concurrent transfers by locking wallets in consistent ID order
        Wallet wallet1 = sender.getWallet();
        Wallet wallet2 = receiver.getWallet();
        Wallet firstLock = wallet1.getWalletId().compareTo(wallet2.getWalletId()) < 0 ? wallet1 : wallet2;
        Wallet secondLock = (firstLock == wallet1) ? wallet2 : wallet1;

        synchronized (firstLock) {
            synchronized (secondLock) {
                // Re-verify balance inside synchronized block for race conditions
                if (!wallet1.debit(transferAmount)) {
                    return recordAndReturnFailure(txnId, senderPhone, sender.getFullName(), receiverPhone, receiver.getFullName(),
                            transferAmount, now, "Debit failed due to insufficient funds.");
                }

                try {
                    // Credit receiver wallet
                    wallet2.credit(transferAmount);
                } catch (Exception ex) {
                    // Rollback debit atomically if credit throws an unexpected error
                    wallet1.credit(transferAmount);
                    return recordAndReturnFailure(txnId, senderPhone, sender.getFullName(), receiverPhone, receiver.getFullName(),
                            transferAmount, now, "Transfer failed during credit. Rolled back successfully: " + ex.getMessage());
                }
            }
        }

        // 11. Create and Store SUCCESS Transaction Record (Requirements 10, 11, 12, 13)
        MoneyTransferTransaction successTxn = new MoneyTransferTransaction(
                txnId,
                senderPhone,
                sender.getFullName(),
                receiverPhone,
                receiver.getFullName(),
                transferAmount,
                now,
                TransactionStatus.SUCCESS,
                "Transfer completed successfully"
        );
        dataStore.saveTransaction(successTxn);

        String successMsg = String.format("₹%.2f transferred successfully to %s (%s). Updated Sender Balance: ₹%.2f.",
                transferAmount, receiver.getFullName(), receiverPhone, sender.getWallet().getBalance());
        return new TransferResult(true, successMsg, successTxn);
    }

    @Override
    public boolean reverseTransaction(String transactionId, String reason) {
        if (transactionId == null) return false;

        Optional<Transaction> txnOpt = dataStore.findTransactionById(transactionId);
        if (txnOpt.isEmpty()) return false;

        Transaction txn = txnOpt.get();
        if (!(txn instanceof MoneyTransferTransaction transferTxn)) {
            return false;
        }

        // Only SUCCESS transactions can be reversed
        if (transferTxn.getStatus() != TransactionStatus.SUCCESS) {
            return false;
        }

        Optional<User> senderOpt = dataStore.findUserByPhone(transferTxn.getSenderPhone());
        Optional<User> receiverOpt = dataStore.findUserByPhone(transferTxn.getReceiverPhone());

        if (senderOpt.isEmpty() || receiverOpt.isEmpty()) {
            return false;
        }

        User sender = senderOpt.get();
        User receiver = receiverOpt.get();
        BigDecimal amount = transferTxn.getAmount();

        Wallet senderWallet = sender.getWallet();
        Wallet receiverWallet = receiver.getWallet();

        Wallet firstLock = senderWallet.getWalletId().compareTo(receiverWallet.getWalletId()) < 0 ? senderWallet : receiverWallet;
        Wallet secondLock = (firstLock == senderWallet) ? receiverWallet : senderWallet;

        synchronized (firstLock) {
            synchronized (secondLock) {
                // Ensure receiver has the funds to return
                if (!receiverWallet.debit(amount)) {
                    return false;
                }
                senderWallet.credit(amount);
            }
        }

        // Update status to REVERSED (Requirement 12)
        transferTxn.setStatus(TransactionStatus.REVERSED);
        transferTxn.setRemarks("Reversed: " + (reason != null ? reason : "Administrative Reversal"));
        return true;
    }

    @Override
    public BigDecimal getDailySuccessfulTransferTotal(String senderPhone) {
        if (senderPhone == null) return BigDecimal.ZERO;

        LocalDate today = LocalDate.now();
        BigDecimal total = BigDecimal.ZERO;

        for (Transaction txn : dataStore.getAllTransactions()) {
            if (txn instanceof MoneyTransferTransaction mtt) {
                if (mtt.getSenderPhone().equals(senderPhone.trim())
                        && mtt.getStatus() == TransactionStatus.SUCCESS
                        && mtt.getTimestamp().toLocalDate().isEqual(today)) {
                    total = total.add(mtt.getAmount());
                }
            }
        }
        return total.setScale(2, RoundingMode.HALF_UP);
    }

    /**
     * Generates a unique 16-character alphanumeric UPI reference ID.
     */
    private String generateReferenceId() {
        long timestamp = System.currentTimeMillis();
        int randNum = 1000 + random.nextInt(9000);
        return "UPI" + timestamp + randNum;
    }

    /**
     * Helper to store FAILED transaction and return failed TransferResult.
     */
    private TransferResult recordAndReturnFailure(String txnId,
                                                  String senderPhone, String senderName,
                                                  String receiverPhone, String receiverName,
                                                  BigDecimal amount, LocalDateTime timestamp,
                                                  String failureReason) {
        MoneyTransferTransaction failedTxn = new MoneyTransferTransaction(
                txnId,
                senderPhone,
                senderName,
                receiverPhone,
                receiverName,
                amount,
                timestamp,
                TransactionStatus.FAILED,
                failureReason
        );
        dataStore.saveTransaction(failedTxn);
        return new TransferResult(false, failureReason, failedTxn);
    }
}
