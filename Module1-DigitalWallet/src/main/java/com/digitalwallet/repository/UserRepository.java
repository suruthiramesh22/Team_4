package com.digitalwallet.repository;

import com.digitalwallet.model.Customer;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class UserRepository {
    private final Map<String, Customer> usersByPhone = new HashMap<>();
    private final Map<String, Customer> usersById = new HashMap<>();

    public void save(Customer customer) {
        if (customer == null) {
            throw new IllegalArgumentException("Customer is required.");
        }
        if (usersByPhone.containsKey(customer.getPhone())) {
            throw new IllegalArgumentException("Phone number is already registered.");
        }
        if (usersById.containsKey(customer.getUserId())) {
            throw new IllegalArgumentException("User ID already exists.");
        }
        usersByPhone.put(customer.getPhone(), customer);
        usersById.put(customer.getUserId(), customer);
    }

    public Customer findByPhone(String phone) {
        return usersByPhone.get(phone);
    }

    public Customer findById(String userId) {
        return usersById.get(userId);
    }

    public boolean existsByPhone(String phone) {
        return usersByPhone.containsKey(phone);
    }

    public Map<String, Customer> findAll() {
        return Collections.unmodifiableMap(usersByPhone);
    }
}
