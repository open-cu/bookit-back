package com.opencu.bookit.adapter.out.security.spring.service;

import com.opencu.bookit.domain.model.user.Role;
import com.opencu.bookit.domain.model.user.UserModel;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component("securityService")
public class SecurityService {
    @Value("${spring.profiles.active:}")
    private String activeProfile;

    private static final String PROFILE_DEV = "dev";
    private static final String ADMIN = "ROLE_ADMIN";
    private static final String SUPERADMIN = "ROLE_SUPERADMIN";

    public boolean isDev() {
        return PROFILE_DEV.equalsIgnoreCase(activeProfile);
    }

    public static String getAdmin() {
        return ADMIN;
    }

    public static String getSuperadmin() {
        return SUPERADMIN;
    }

    public boolean hasRequiredRole(String role) {
        UserModel user = (UserModel) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return user.getRoles().contains(Role.fromString(role));
    }
}
