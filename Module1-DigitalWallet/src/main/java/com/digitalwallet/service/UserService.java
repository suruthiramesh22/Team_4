package com.digitalwallet.service;

import com.digitalwallet.model.Customer;
import com.digitalwallet.model.KYCStatus;
import com.digitalwallet.model.UserStatus;
import com.digitalwallet.repository.UserRepository;

public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public Customer register(String userId, String name, String phone,
                             String email, String pin) {
        if (phone == null || !phone.matches("[0-9]{10}")) {
            throw new IllegalArgumentException("Phone number must contain exactly 10 digits.");
        }
        if (pin == null || !pin.matches("[0-9]{4}")) {
            throw new IllegalArgumentException("PIN must contain exactly 4 digits.");
        }
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Name is required.");
        }
        if (email == null || !email.matches("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$")) {
            throw new IllegalArgumentException("Enter a valid email address.");
        }

        Customer customer = new Customer(userId, name, phone, email, pin);
        userRepository.save(customer);
        return customer;
    }

    public Customer findUser(String phone) {
        Customer customer = userRepository.findByPhone(phone);
        if (customer == null) {
            throw new IllegalArgumentException("No customer found for phone: " + phone);
        }
        return customer;
    }

    public void updateKyc(String phone, KYCStatus status) {
        findUser(phone).setKycStatus(status);
    }

    public void updateStatus(String phone, UserStatus status) {
        findUser(phone).setUserStatus(status);
    }

    public void displayUser(String phone) {
        Customer customer = findUser(phone);
        System.out.println("\n========== CUSTOMER DETAILS ==========");
        System.out.println("User ID       : " + customer.getUserId());
        System.out.println("Name          : " + customer.getName());
        System.out.println("Phone         : " + customer.getPhone());
        System.out.println("Email         : " + customer.getEmail());
        System.out.println("Role          : " + customer.getRole());
        System.out.println("KYC           : " + customer.getKycStatus());
        System.out.println("User status   : " + customer.getUserStatus());
        System.out.println("Wallet ID     : " + customer.getWallet().getAccountId());
        System.out.println("Wallet balance: Rs. " + customer.getWallet().getBalance());
        System.out.println("Wallet limit  : Rs. " + customer.getWalletLimit());
        System.out.println("======================================");
    }
}
