package com.opencu.bookit.adapter.out.security.spring.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component("securityService")
public class SecurityService {
    @Value("${spring.profiles.active:}")
    private String activeProfile;

    public boolean hasRoleAdminOrIsDev() {
        return "dev".equalsIgnoreCase(activeProfile) ||
                SecurityContextHolder.getContext().getAuthentication().getAuthorities()
                        .stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
    }

    public boolean hasRoleSuperAdminOrIsDev() {
        return "dev".equalsIgnoreCase(activeProfile) ||
                SecurityContextHolder.getContext().getAuthentication().getAuthorities()
                        .stream().anyMatch(a -> a.getAuthority().equals("ROLE_SUPERADMIN"));
    }
}
