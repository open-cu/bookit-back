package com.opencu.bookit.application.port.out.user;

import com.opencu.bookit.domain.model.user.UserModel;

import java.util.Optional;
import java.util.UUID;

public interface LoadUserPort {
    UserModel findByName(String name);
    Optional<UserModel> findByUsername(String username);
    Optional<UserModel> findById(UUID id);
}