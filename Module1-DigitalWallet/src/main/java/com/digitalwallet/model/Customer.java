package com.digitalwallet.model;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

public class Customer extends User {
    private KYCStatus kycStatus;
    private UserStatus userStatus;
    private final Wallet wallet;
    private final Map<String, BankAccount> bankAccounts = new LinkedHashMap<>();

    public Customer(String userId, String name, String phone, String email, String pin) {
        super(userId, name, phone, email, pin);
        this.kycStatus = KYCStatus.BASIC;
        this.userStatus = UserStatus.ACTIVE;
        this.wallet = new Wallet("W-" + userId, userId);
    }

    public KYCStatus getKycStatus() { return kycStatus; }
    public UserStatus getUserStatus() { return userStatus; }
    public Wallet getWallet() { return wallet; }

    // Read-only view prevents callers from adding/removing accounts behind the service's back.
    public Map<String, BankAccount> getBankAccounts() {
        return Collections.unmodifiableMap(bankAccounts);
    }

    public BigDecimal getWalletLimit() {
        return kycStatus == KYCStatus.BASIC
                ? new BigDecimal("10000.00")
                : new BigDecimal("100000.00");
    }

    public boolean canHoldAmount(BigDecimal amount) {
        return amount != null
                && amount.signum() >= 0
                && wallet.getBalance().add(amount).compareTo(getWalletLimit()) <= 0;
    }

    public void setKycStatus(KYCStatus newStatus) {
        if (newStatus == null) {
            throw new IllegalArgumentException("KYC status is required.");
        }
        if (newStatus == KYCStatus.BASIC
                && wallet.getBalance().compareTo(new BigDecimal("10000.00")) > 0) {
            throw new IllegalStateException(
                    "Cannot downgrade to BASIC: wallet balance is above Rs. 10,000.");
        }
        this.kycStatus = newStatus;
    }

    public void setUserStatus(UserStatus newStatus) {
        if (newStatus == null) {
            throw new IllegalArgumentException("User status is required.");
        }
        this.userStatus = newStatus;
    }

    public void addBankAccount(BankAccount account) {
        if (account == null) {
            throw new IllegalArgumentException("Bank account is required.");
        }
        if (bankAccounts.size() >= 3) {
            throw new IllegalStateException("A customer can link at most 3 bank accounts.");
        }
        if (bankAccounts.containsKey(account.getAccountNumber())) {
            throw new IllegalStateException("This bank account is already linked to this customer.");
        }

        bankAccounts.put(account.getAccountNumber(), account);
        if (bankAccounts.size() == 1) {
            account.setPrimary(true);
        }
    }

    public void removeBankAccount(String accountNumber) {
        BankAccount account = bankAccounts.get(accountNumber);
        if (account == null) {
            throw new IllegalArgumentException("Bank account is not linked to this customer.");
        }
        if (account.getPendingTransactions() > 0) {
            throw new IllegalStateException("Cannot unlink an account with pending transactions.");
        }

        boolean wasPrimary = account.isPrimary();
        bankAccounts.remove(accountNumber);

        if (wasPrimary && !bankAccounts.isEmpty()) {
            bankAccounts.values().iterator().next().setPrimary(true);
        }
    }

    public void makePrimary(String accountNumber) {
        BankAccount selected = bankAccounts.get(accountNumber);
        if (selected == null) {
            throw new IllegalArgumentException("Bank account is not linked to this customer.");
        }
        for (BankAccount account : bankAccounts.values()) {
            account.setPrimary(false);
        }
        selected.setPrimary(true);
    }

    @Override
    public String getRole() {
        return "CUSTOMER";
    }
}
