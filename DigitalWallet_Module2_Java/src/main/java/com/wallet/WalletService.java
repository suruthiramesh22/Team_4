package com.wallet;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

public class WalletService {
    private final List<Transaction> transactions = new ArrayList<>();
    private final List<String> allBankAccountNumbers = new ArrayList<>();

    public void registerBankAccountGlobally(BankAccount account) {
        if (allBankAccountNumbers.contains(account.getAccountNumber())) {
            throw new IllegalStateException("Bank account number must be unique in the system.");
        }
        allBankAccountNumbers.add(account.getAccountNumber());
    }

    public Transaction addMoney(User user, String accountNumber, double amount) {
        BankAccount bank = null;
        try {
            validateActive(user);
            validateAddMoneyAmount(amount);
            bank = user.findBankAccount(accountNumber);
            if (user.getWallet().getBalance() + amount > user.getKycStatus().getWalletLimit()) {
                throw new IllegalStateException("Wallet limit exceeded for " + user.getKycStatus() + " KYC.");
            }
            // Check before changing either balance, so a rejected operation changes neither.
            if (bank.getBalance() < amount) throw new IllegalStateException("Insufficient bank balance.");
            bank.debit(amount);
            user.getWallet().credit(amount, user.getKycStatus().getWalletLimit());
            return record(user, TransactionType.ADD_MONEY, accountNumber, amount, TransactionStatus.SUCCESS, "Money added successfully.");
        } catch (RuntimeException ex) {
            return record(user, TransactionType.ADD_MONEY, accountNumber, amount, TransactionStatus.FAILED, ex.getMessage());
        }
    }

    public Transaction withdraw(User user, String accountNumber, double amount) {
        try {
            validateActive(user);
            if (!Double.isFinite(amount) || amount <= 0) throw new IllegalArgumentException("Withdrawal amount must be greater than zero.");
            BankAccount bank = user.findBankAccount(accountNumber);
            if (user.getWallet().getBalance() < amount) throw new IllegalStateException("Insufficient wallet balance.");
            // Validate first, then perform both changes.
            user.getWallet().debit(amount);
            bank.credit(amount);
            return record(user, TransactionType.WITHDRAW, accountNumber, amount, TransactionStatus.SUCCESS, "Withdrawal successful.");
        } catch (RuntimeException ex) {
            return record(user, TransactionType.WITHDRAW, accountNumber, amount, TransactionStatus.FAILED, ex.getMessage());
        }
    }

    private void validateActive(User user) {
        if (user.getStatus() != UserStatus.ACTIVE) throw new IllegalStateException("Blocked users cannot make transactions.");
    }

    private void validateAddMoneyAmount(double amount) {
        if (!Double.isFinite(amount) || amount < 10 || amount > 50_000) {
            throw new IllegalArgumentException("Add-money amount must be between Rs. 10 and Rs. 50,000.");
        }
    }

    private Transaction record(User user, TransactionType type, String accountNumber, double amount,
                               TransactionStatus status, String message) {
        Transaction tx = new Transaction("TXN-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase(),
                user.getId(), type, accountNumber == null ? "-" : accountNumber, amount, status, message);
        transactions.add(tx);
        return tx;
    }

    public List<Transaction> getHistoryLatestFirst(long userId) {
        return transactions.stream()
                .filter(t -> t.getUserId() == userId)
                .sorted(Comparator.comparing(Transaction::getTimestamp).reversed())
                .toList();
    }
}
