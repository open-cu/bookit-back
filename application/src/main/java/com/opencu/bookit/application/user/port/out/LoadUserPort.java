package com.opencu.bookit.application.user.port.out;

import com.opencu.bookit.domain.model.user.User;

import java.util.Optional;
import java.util.UUID;

public interface LoadUserPort {
    User findByName(String name);
    Optional<User> findByUsername(String username);
    Optional<User> findById(UUID id);
}