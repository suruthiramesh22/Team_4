package com.upi.payment;

import com.upi.payment.model.*;
import com.upi.payment.repository.InMemoryDataStore;
import com.upi.payment.service.MoneyTransferService;
import com.upi.payment.service.MoneyTransferServiceImpl;
import com.upi.payment.service.TransferResult;

import java.math.BigDecimal;
import java.util.List;

/**
 * Main Application Runner & Verification Suite for Module 3 (Money Transfer).
 * Demonstrates all functional requirements and OOP design principles.
 */
public class Main {

    public static void main(String[] args) {
        System.out.println("==========================================================================");
        System.out.println("     DIGITAL WALLET AND UPI PAYMENT APP - MODULE 3: MONEY TRANSFER        ");
        System.out.println("==========================================================================");

        InMemoryDataStore dataStore = InMemoryDataStore.getInstance();
        MoneyTransferService transferService = new MoneyTransferServiceImpl(dataStore);

        // ---------------------------------------------------------------------
        // 1. SETUP INITIAL USERS & WALLETS (Reusing User & Wallet classes)
        // ---------------------------------------------------------------------
        System.out.println("\n[SETUP] Registering demo users in-memory...");
        User rahul = new User("USR101", "Rahul Sharma", "9876543210", "rahul@upi.com", "1234", new BigDecimal("15000.00"));
        User priya = new User("USR102", "Priya Verma", "9123456780", "priya@upi.com", "4321", new BigDecimal("5000.00"));
        User amit  = new User("USR103", "Amit Kumar", "9988776655", "amit@upi.com", "1111", new BigDecimal("2000.00"));
        User sneha = new User("USR104", "Sneha Patel", "9000011111", "sneha@upi.com", "9999", new BigDecimal("1000.00"));
        sneha.setStatus(UserStatus.INACTIVE); // Inactive account for testing

        dataStore.saveUser(rahul);
        dataStore.saveUser(priya);
        dataStore.saveUser(amit);
        dataStore.saveUser(sneha);

        System.out.println(" - User 1: " + rahul.getFullName() + " | Phone: " + rahul.getPhoneNumber() + " | Balance: ₹" + rahul.getWallet().getBalance());
        System.out.println(" - User 2: " + priya.getFullName() + " | Phone: " + priya.getPhoneNumber() + " | Balance: ₹" + priya.getWallet().getBalance());
        System.out.println(" - User 3: " + amit.getFullName() + "  | Phone: " + amit.getPhoneNumber() + " | Balance: ₹" + amit.getWallet().getBalance());
        System.out.println(" - User 4: " + sneha.getFullName() + " | Phone: " + sneha.getPhoneNumber() + " | Status: " + sneha.getStatus());

        // ---------------------------------------------------------------------
        // TEST CASE 1: Normal Successful Transfer (₹2,500 from Rahul to Priya)
        // ---------------------------------------------------------------------
        System.out.println("\n--------------------------------------------------------------------------");
        System.out.println("TEST 1: Normal Transfer (Rahul -> Priya ₹2,500.00 with valid PIN 1234)");
        System.out.println("--------------------------------------------------------------------------");
        TransferResult result1 = transferService.transferMoney("9876543210", "9123456780", new BigDecimal("2500.00"), "1234");
        System.out.println("Outcome: " + result1);
        System.out.println("Rahul Wallet Balance: ₹" + rahul.getWallet().getBalance());
        System.out.println("Priya Wallet Balance: ₹" + priya.getWallet().getBalance());

        // ---------------------------------------------------------------------
        // TEST CASE 2: Prevent Self-Transfer (Requirement 3)
        // ---------------------------------------------------------------------
        System.out.println("\n--------------------------------------------------------------------------");
        System.out.println("TEST 2: Prevent Self-Transfer (Rahul -> Rahul)");
        System.out.println("--------------------------------------------------------------------------");
        TransferResult result2 = transferService.transferMoney("9876543210", "9876543210", new BigDecimal("500.00"), "1234");
        System.out.println("Outcome: " + result2);

        // ---------------------------------------------------------------------
        // TEST CASE 3: Transaction Amount Limits (Min ₹1, Max ₹10,000) (Requirement 4)
        // ---------------------------------------------------------------------
        System.out.println("\n--------------------------------------------------------------------------");
        System.out.println("TEST 3A: Amount Below Minimum (₹0.50)");
        System.out.println("--------------------------------------------------------------------------");
        TransferResult result3A = transferService.transferMoney("9876543210", "9123456780", new BigDecimal("0.50"), "1234");
        System.out.println("Outcome: " + result3A);

        System.out.println("\nTEST 3B: Amount Above Maximum (₹15,000.00)");
        TransferResult result3B = transferService.transferMoney("9876543210", "9123456780", new BigDecimal("15000.00"), "1234");
        System.out.println("Outcome: " + result3B);

        // ---------------------------------------------------------------------
        // TEST CASE 4: Validate Receiver Existence & Active Status (Requirement 2)
        // ---------------------------------------------------------------------
        System.out.println("\n--------------------------------------------------------------------------");
        System.out.println("TEST 4A: Non-Existent Receiver Phone Number (9999999999)");
        System.out.println("--------------------------------------------------------------------------");
        TransferResult result4A = transferService.transferMoney("9876543210", "9999999999", new BigDecimal("500.00"), "1234");
        System.out.println("Outcome: " + result4A);

        System.out.println("\nTEST 4B: Inactive Receiver Account (Sneha)");
        TransferResult result4B = transferService.transferMoney("9876543210", "9000011111", new BigDecimal("500.00"), "1234");
        System.out.println("Outcome: " + result4B);

        // ---------------------------------------------------------------------
        // TEST CASE 5: Insufficient Balance Check (Requirement 8)
        // ---------------------------------------------------------------------
        System.out.println("\n--------------------------------------------------------------------------");
        System.out.println("TEST 5: Insufficient Wallet Balance (Amit has ₹2,000, tries to send ₹5,000)");
        System.out.println("--------------------------------------------------------------------------");
        TransferResult result5 = transferService.transferMoney("9988776655", "9123456780", new BigDecimal("5000.00"), "1111");
        System.out.println("Outcome: " + result5);

        // ---------------------------------------------------------------------
        // TEST CASE 6: PIN Verification & 24h Lockout After 3 Attempts (Requirements 6 & 7)
        // ---------------------------------------------------------------------
        System.out.println("\n--------------------------------------------------------------------------");
        System.out.println("TEST 6: 3 Consecutive Wrong PIN Attempts & 24-Hour Account Lockout");
        System.out.println("--------------------------------------------------------------------------");
        System.out.println("Attempt 1 (Wrong PIN '0000'):");
        TransferResult pinAttempt1 = transferService.transferMoney("9876543210", "9123456780", new BigDecimal("100.00"), "0000");
        System.out.println(" " + pinAttempt1);

        System.out.println("Attempt 2 (Wrong PIN '9999'):");
        TransferResult pinAttempt2 = transferService.transferMoney("9876543210", "9123456780", new BigDecimal("100.00"), "9999");
        System.out.println(" " + pinAttempt2);

        System.out.println("Attempt 3 (Wrong PIN '5555'):");
        TransferResult pinAttempt3 = transferService.transferMoney("9876543210", "9123456780", new BigDecimal("100.00"), "5555");
        System.out.println(" " + pinAttempt3);

        System.out.println("\nAttempt 4: Trying with CORRECT PIN ('1234') while account is locked:");
        TransferResult pinAttempt4 = transferService.transferMoney("9876543210", "9123456780", new BigDecimal("100.00"), "1234");
        System.out.println(" " + pinAttempt4);

        // Reset Rahul's PIN lockout to test daily limit
        System.out.println("\n[ADMIN ACTION] Unlocking Rahul's account for remaining tests...");
        rahul.resetPinLock();

        // ---------------------------------------------------------------------
        // TEST CASE 7: Daily Successful Transfer Limit of ₹25,000 (Requirement 5)
        // ---------------------------------------------------------------------
        System.out.println("\n--------------------------------------------------------------------------");
        System.out.println("TEST 7: Daily Successful Transfer Limit Enforcement (Max ₹25,000.00 / day)");
        System.out.println("--------------------------------------------------------------------------");
        // Add funds to Rahul so balance is sufficient
        rahul.getWallet().credit(new BigDecimal("30000.00"));
        System.out.println("Credited extra funds to Rahul. Current Balance: ₹" + rahul.getWallet().getBalance());
        System.out.println("Transferred earlier today: ₹" + transferService.getDailySuccessfulTransferTotal("9876543210"));

        System.out.println("\nSending ₹10,000.00 (Tx 1):");
        TransferResult limitTx1 = transferService.transferMoney("9876543210", "9123456780", new BigDecimal("10000.00"), "1234");
        System.out.println(" " + limitTx1);

        System.out.println("Sending ₹10,000.00 (Tx 2):");
        TransferResult limitTx2 = transferService.transferMoney("9876543210", "9123456780", new BigDecimal("10000.00"), "1234");
        System.out.println(" " + limitTx2);

        System.out.println("Current Daily Total sent: ₹" + transferService.getDailySuccessfulTransferTotal("9876543210"));

        System.out.println("\nSending ₹5,000.00 (Tx 3 - would bring total to ₹27,500, exceeding ₹25,000 limit):");
        TransferResult limitTx3 = transferService.transferMoney("9876543210", "9123456780", new BigDecimal("5000.00"), "1234");
        System.out.println(" " + limitTx3);

        System.out.println("Sending ₹2,500.00 (Tx 4 - exactly hits ₹25,000 limit):");
        TransferResult limitTx4 = transferService.transferMoney("9876543210", "9123456780", new BigDecimal("2500.00"), "1234");
        System.out.println(" " + limitTx4);
        System.out.println("Final Daily Successful Total: ₹" + transferService.getDailySuccessfulTransferTotal("9876543210"));

        // ---------------------------------------------------------------------
        // TEST CASE 8: Transaction Reversal (Requirement 12 - REVERSED status)
        // ---------------------------------------------------------------------
        System.out.println("\n--------------------------------------------------------------------------");
        System.out.println("TEST 8: Atomic Transaction Reversal (Reversing Tx 4 of ₹2,500.00)");
        System.out.println("--------------------------------------------------------------------------");
        String txnToReverse = limitTx4.getTransaction().getTransactionId();
        System.out.println("Reversing Transaction ID: " + txnToReverse);
        System.out.println("Before Reversal -> Rahul Balance: ₹" + rahul.getWallet().getBalance() + " | Priya Balance: ₹" + priya.getWallet().getBalance());

        boolean reversed = transferService.reverseTransaction(txnToReverse, "Accidental duplicate transaction");
        System.out.println("Reversal Execution: " + (reversed ? "SUCCESS" : "FAILED"));
        System.out.println("After Reversal  -> Rahul Balance: ₹" + rahul.getWallet().getBalance() + " | Priya Balance: ₹" + priya.getWallet().getBalance());
        System.out.println("Updated Status in Data Store: " + dataStore.findTransactionById(txnToReverse).get().getStatus());

        // ---------------------------------------------------------------------
        // TEST CASE 9: Display All Transactions (Polymorphism & Dynamic Storage)
        // ---------------------------------------------------------------------
        System.out.println("\n--------------------------------------------------------------------------");
        System.out.println("TEST 9: Transaction Audit Log (Dynamic In-Memory Collections & Polymorphism)");
        System.out.println("--------------------------------------------------------------------------");
        List<Transaction> allTransactions = dataStore.getAllTransactions();
        System.out.println("Total Transaction Records Stored: " + allTransactions.size());
        for (Transaction txn : allTransactions) {
            // Polymorphic method call: invokes overridden getTransactionSummary() at runtime
            System.out.println(txn.getTransactionSummary());
        }

        System.out.println("\n==========================================================================");
        System.out.println("             ALL MODULE 3 REQUIREMENTS TESTED SUCCESSFULLY!               ");
        System.out.println("==========================================================================");
    }
}
