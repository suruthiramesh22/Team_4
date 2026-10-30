package com.digitalwallet.service;

import com.digitalwallet.model.BankAccount;
import com.digitalwallet.model.Customer;
import com.digitalwallet.repository.BankAccountRepository;
import com.digitalwallet.repository.UserRepository;
import java.math.BigDecimal;

public class BankAccountService {
    private final UserRepository userRepository;
    private final BankAccountRepository bankRepository;

    public BankAccountService(UserRepository userRepository,
                              BankAccountRepository bankRepository) {
        this.userRepository = userRepository;
        this.bankRepository = bankRepository;
    }

    public void linkBankAccount(String phone, String accountId, String bankName,
                                String accountNumber, BigDecimal openingBalance) {
        Customer customer = userRepository.findByPhone(phone);
        if (customer == null) {
            throw new IllegalArgumentException("Customer not found.");
        }
        if (customer.getBankAccounts().size() >= 3) {
            throw new IllegalStateException("A customer can link at most 3 bank accounts.");
        }
        if (bankRepository.findByNumber(accountNumber) != null) {
            throw new IllegalArgumentException("Bank account number already exists in the system.");
        }

        BankAccount account = new BankAccount(
                accountId, bankName, accountNumber, openingBalance);

        // Save globally and attach to customer. Roll back global save if attachment fails.
        bankRepository.save(account);
        try {
            customer.addBankAccount(account);
        } catch (RuntimeException exception) {
            bankRepository.delete(accountNumber);
            throw exception;
        }
    }

    public void unlinkBankAccount(String phone, String accountNumber) {
        Customer customer = userRepository.findByPhone(phone);
        if (customer == null) {
            throw new IllegalArgumentException("Customer not found.");
        }
        // Customer validates ownership and pending-transaction count before removal.
        customer.removeBankAccount(accountNumber);
        bankRepository.delete(accountNumber);
    }

    public void changePrimary(String phone, String accountNumber) {
        Customer customer = userRepository.findByPhone(phone);
        if (customer == null) {
            throw new IllegalArgumentException("Customer not found.");
        }
        customer.makePrimary(accountNumber);
    }

    public void displayAccounts(String phone) {
        Customer customer = userRepository.findByPhone(phone);
        if (customer == null) {
            throw new IllegalArgumentException("Customer not found.");
        }

        System.out.println("\n========== LINKED BANK ACCOUNTS ==========");
        if (customer.getBankAccounts().isEmpty()) {
            System.out.println("No bank accounts linked.");
        } else {
            for (BankAccount account : customer.getBankAccounts().values()) {
                System.out.println("Bank             : " + account.getBankName());
                System.out.println("Account number   : " + maskAccountNumber(account.getAccountNumber()));
                System.out.println("Balance          : Rs. " + account.getBalance());
                System.out.println("Primary          : " + (account.isPrimary() ? "Yes" : "No"));
                System.out.println("Pending operations: " + account.getPendingTransactions());
                System.out.println("------------------------------------------");
            }
        }
    }

    // Demonstration hook for testing the Module 1 unlink restriction.
    // In the integrated project, transaction services should call these methods.
    public void markPending(String accountNumber) {
        BankAccount account = requireGlobalAccount(accountNumber);
        account.addPendingTransaction();
    }

    public void completePending(String accountNumber) {
        BankAccount account = requireGlobalAccount(accountNumber);
        account.completePendingTransaction();
    }

    private BankAccount requireGlobalAccount(String accountNumber) {
        BankAccount account = bankRepository.findByNumber(accountNumber);
        if (account == null) {
            throw new IllegalArgumentException("Bank account not found in system.");
        }
        return account;
    }

    private String maskAccountNumber(String accountNumber) {
        int visibleDigits = Math.min(4, accountNumber.length());
        return "*".repeat(accountNumber.length() - visibleDigits)
                + accountNumber.substring(accountNumber.length() - visibleDigits);
    }
}
