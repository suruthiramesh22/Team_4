package com.wallet;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class User {
    private final long id;
    private final String name;
    private final String phone;
    private final String email;
    private final String pin;
    private final KycStatus kycStatus;
    private UserStatus status = UserStatus.ACTIVE;
    private final Wallet wallet = new Wallet();
    private final List<BankAccount> bankAccounts = new ArrayList<>();

    public User(long id, String name, String phone, String email, String pin, KycStatus kycStatus) {
        if (name == null || name.isBlank()) throw new IllegalArgumentException("Name is required.");
        if (phone == null || !phone.matches("\\d{10}")) throw new IllegalArgumentException("Phone must contain 10 digits.");
        if (email == null || !email.contains("@")) throw new IllegalArgumentException("Enter a valid email.");
        if (pin == null || !pin.matches("\\d{4}")) throw new IllegalArgumentException("PIN must contain 4 digits.");
        this.id = id;
        this.name = name;
        this.phone = phone;
        this.email = email;
        this.pin = pin;
        this.kycStatus = kycStatus;
    }

    public long getId() { return id; }
    public String getName() { return name; }
    public String getPhone() { return phone; }
    public String getEmail() { return email; }
    public KycStatus getKycStatus() { return kycStatus; }
    public UserStatus getStatus() { return status; }
    public void setStatus(UserStatus status) { this.status = status; }
    public Wallet getWallet() { return wallet; }
    public List<BankAccount> getBankAccounts() { return Collections.unmodifiableList(bankAccounts); }

    public boolean checkPin(String enteredPin) { return pin.equals(enteredPin); }

    public void linkBankAccount(BankAccount account) {
        if (bankAccounts.size() >= 3) throw new IllegalStateException("A user can link at most 3 bank accounts.");
        if (bankAccounts.stream().anyMatch(a -> a.getAccountNumber().equals(account.getAccountNumber()))) {
            throw new IllegalStateException("This bank account is already linked to this user.");
        }
        if (account.isPrimary()) bankAccounts.forEach(a -> a.setPrimary(false));
        if (bankAccounts.isEmpty()) account.setPrimary(true);
        bankAccounts.add(account);
    }

    public BankAccount findBankAccount(String accountNumber) {
        return bankAccounts.stream()
                .filter(a -> a.getAccountNumber().equals(accountNumber))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Bank account is not linked to this user."));
    }

    public void unlinkBankAccount(String accountNumber) {
        BankAccount account = findBankAccount(accountNumber);
        if (account.hasPendingTransaction()) throw new IllegalStateException("Cannot unlink an account with a pending transaction.");
        bankAccounts.remove(account);
        if (account.isPrimary() && !bankAccounts.isEmpty()) bankAccounts.get(0).setPrimary(true);
    }
}
