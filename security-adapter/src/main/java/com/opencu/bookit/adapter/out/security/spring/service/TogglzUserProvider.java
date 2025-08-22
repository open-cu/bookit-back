package com.opencu.bookit.adapter.out.security.spring.service;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.togglz.core.user.FeatureUser;
import org.togglz.core.user.SimpleFeatureUser;
import org.togglz.core.user.UserProvider;

@Component
public class TogglzUserProvider implements UserProvider {

    SecurityService securityService;

    public TogglzUserProvider(SecurityService securityService) {
        this.securityService = securityService;
    }

    @Override
    public FeatureUser getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            return null;
        }

        String username = getUsername(authentication);
        if (username == null) {
            return null;
        }

        boolean isFeatureAdmin = securityService.hasRequiredRole(SecurityService.getAdmin()) ||
                                 securityService.isDev();

        return new SimpleFeatureUser(username, isFeatureAdmin);
    }

    private String getUsername(Authentication authentication) {
        Object principal = authentication.getPrincipal();
        if (principal instanceof UserDetails) {
            return ((UserDetails) principal).getUsername();
        }
        return principal.toString();
    }
}