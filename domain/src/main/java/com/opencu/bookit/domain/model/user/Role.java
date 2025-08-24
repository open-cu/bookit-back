package com.opencu.bookit.domain.model.user;

public enum Role {
    ROLE_SYSTEM_USER,
    ROLE_USER,
    ROLE_ADMIN,
    ROLE_SUPERADMIN;

    public static Role fromString(String roleStr) {
        if (roleStr == null) {
            return null;
        }
        try {
            return Role.valueOf(roleStr.toUpperCase());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}
