package com.opencu.bookit.application.user.service;

import com.opencu.bookit.application.user.port.out.LoadAuthorizationInfo;
import com.opencu.bookit.application.user.port.out.LoadUserPort;
import com.opencu.bookit.application.user.port.out.SaveUserPort;
import com.opencu.bookit.domain.model.user.User;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Service
public class UserService {
    LoadUserPort loadUserPort;
    SaveUserPort saveUserPort;
    LoadAuthorizationInfo loadAuthorizationInfo;

    public UserService(LoadUserPort loadUserPort) {
        this.loadUserPort = loadUserPort;
    }

    public Optional<User> findById(UUID id) {
        return loadUserPort.findById(id);
    }
    public UUID getTestUserId() {
        return loadUserPort.findByName("Alice Johnson").getId();
    }

    public User getCurrentUser() {
        //String username = SecurityContextHolder.getContext().getAuthentication().getName();
        String username = loadAuthorizationInfo.getCurrentUsername();
        return loadUserPort.findByUsername(username)
                           .orElseThrow(() -> new RuntimeException("Пользователь не найден: " + username));
    }

    public Optional<User> getUserById(UUID id) {
        return loadUserPort.findById(id);
    }

    @Transactional
    public User updateProfile(String firstName, String lastName, String email, String phone) {
        User user = getCurrentUser();
        if (firstName != null) user.setFirstName(firstName);
        if (lastName != null) user.setLastName(lastName);
        if (email != null) user.setEmail(email);
        if (phone != null) user.setPhone(phone);
        return saveUserPort.save(user);
    }
}
