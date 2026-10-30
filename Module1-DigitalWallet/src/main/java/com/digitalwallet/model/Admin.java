package com.digitalwallet.model;

public class Admin extends User {
    public Admin(String userId, String name, String phone, String email, String pin) {
        super(userId, name, phone, email, pin);
    }

    @Override
    public String getRole() {
        return "ADMIN";
    }
}
