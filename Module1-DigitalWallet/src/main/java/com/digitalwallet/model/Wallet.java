package com.digitalwallet.model;

import java.math.BigDecimal;

public class Wallet extends Account {
    private final String customerId;

    public Wallet(String walletId, String customerId) {
        super(walletId, BigDecimal.ZERO);
        if (customerId == null || customerId.isBlank()) {
            throw new IllegalArgumentException("Customer ID is required.");
        }
        this.customerId = customerId;
    }

    public String getCustomerId() {
        return customerId;
    }

    @Override
    public String getAccountType() {
        return "WALLET";
    }
}
