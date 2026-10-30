package com.digitalwallet.model;

public abstract class User {
    private final String userId;
    private final String name;
    private final String phone;
    private final String email;
    // Simplified for this in-memory academic project. Never store plain PINs in production.
    private final String pin;

    protected User(String userId, String name, String phone, String email, String pin) {
        if (userId == null || userId.isBlank()) {
            throw new IllegalArgumentException("User ID is required.");
        }
        this.userId = userId.trim();
        this.name = requireText(name, "Name");
        this.phone = phone;
        this.email = requireText(email, "Email");
        this.pin = pin;
    }

    private String requireText(String value, String field) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(field + " is required.");
        }
        return value.trim();
    }

    public String getUserId() { return userId; }
    public String getName() { return name; }
    public String getPhone() { return phone; }
    public String getEmail() { return email; }

    public boolean verifyPin(String enteredPin) {
        return pin.equals(enteredPin);
    }

    public abstract String getRole();
}
