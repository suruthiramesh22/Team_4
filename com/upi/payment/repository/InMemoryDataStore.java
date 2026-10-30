package com.upi.payment.repository;

import com.upi.payment.model.Transaction;
import com.upi.payment.model.User;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Thread-Safe In-Memory Data Store.
 * Uses Java Collections (ConcurrentHashMap, ArrayList) for dynamic data management.
 * Follows the Singleton pattern to share data across modules.
 */
public class InMemoryDataStore {
    private static InMemoryDataStore instance;

    // In-memory collections for fast indexed lookups and dynamic storage
    private final Map<String, User> userByPhone = new ConcurrentHashMap<>();
    private final Map<String, User> userById = new ConcurrentHashMap<>();
    private final List<Transaction> transactions = Collections.synchronizedList(new ArrayList<>());

    private InMemoryDataStore() {}

    public static synchronized InMemoryDataStore getInstance() {
        if (instance == null) {
            instance = new InMemoryDataStore();
        }
        return instance;
    }

    /**
     * Stores or updates a user in memory.
     */
    public void saveUser(User user) {
        if (user != null) {
            userByPhone.put(user.getPhoneNumber(), user);
            userById.put(user.getUserId(), user);
        }
    }

    /**
     * Finds user by registered phone number.
     */
    public Optional<User> findUserByPhone(String phoneNumber) {
        if (phoneNumber == null) return Optional.empty();
        return Optional.ofNullable(userByPhone.get(phoneNumber.trim()));
    }

    /**
     * Finds user by internal user ID.
     */
    public Optional<User> findUserById(String userId) {
        if (userId == null) return Optional.empty();
        return Optional.ofNullable(userById.get(userId.trim()));
    }

    /**
     * Returns a copy of all registered users.
     */
    public List<User> getAllUsers() {
        return new ArrayList<>(userByPhone.values());
    }

    /**
     * Appends a transaction record dynamically to the in-memory list.
     */
    public void saveTransaction(Transaction transaction) {
        if (transaction != null) {
            transactions.add(transaction);
        }
    }

    /**
     * Finds a transaction record by its unique reference ID.
     */
    public Optional<Transaction> findTransactionById(String transactionId) {
        if (transactionId == null) return Optional.empty();
        synchronized (transactions) {
            return transactions.stream()
                    .filter(t -> t.getTransactionId().equalsIgnoreCase(transactionId.trim()))
                    .findFirst();
        }
    }

    /**
     * Returns all stored transactions.
     */
    public List<Transaction> getAllTransactions() {
        synchronized (transactions) {
            return new ArrayList<>(transactions);
        }
    }

    /**
     * Clears all collections (useful for test resets).
     */
    public void clearAll() {
        userByPhone.clear();
        userById.clear();
        transactions.clear();
    }
}
