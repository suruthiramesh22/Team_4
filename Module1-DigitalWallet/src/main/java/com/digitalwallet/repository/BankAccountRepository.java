package com.digitalwallet.repository;

import com.digitalwallet.model.BankAccount;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class BankAccountRepository {
    // Keyed by account number so uniqueness is enforced across the whole application.
    private final Map<String, BankAccount> accountsByNumber = new HashMap<>();

    public void save(BankAccount account) {
        if (account == null) {
            throw new IllegalArgumentException("Bank account is required.");
        }
        if (accountsByNumber.containsKey(account.getAccountNumber())) {
            throw new IllegalArgumentException("Bank account number already exists in the system.");
        }
        accountsByNumber.put(account.getAccountNumber(), account);
    }

    public BankAccount findByNumber(String accountNumber) {
        return accountsByNumber.get(accountNumber);
    }

    public void delete(String accountNumber) {
        accountsByNumber.remove(accountNumber);
    }

    public Map<String, BankAccount> findAll() {
        return Collections.unmodifiableMap(accountsByNumber);
    }
}
