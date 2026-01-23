package com.namora.identity.entities;

public enum Role {
    CUSTOMER,
    RIDER,
    ADMIN,
    RESTAURANT_OWNER;

    public static boolean isValidRole(String role) {
        if (role == null) return false;
        try {
            Role.valueOf(role.toUpperCase());
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }
}