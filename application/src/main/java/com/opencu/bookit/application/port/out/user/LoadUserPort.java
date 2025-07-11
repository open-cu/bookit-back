package com.opencu.bookit.application.port.out.user;

import com.opencu.bookit.domain.model.user.UserModel;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.util.Optional;
import java.util.UUID;

public interface LoadUserPort {
    UserModel findByName(String name);
    Optional<UserModel> findByUsername(String username);
    Optional<UserModel> findById(UUID id);
    Optional<UserModel> findByTgId(Long tgId);
    boolean existsByUsername(@NotBlank @Size(min = 3, max = 50) String username);
    boolean existsByEmail(@Email String email);
    boolean existsByPhone(@Pattern(regexp = "^(\\+7\\d{10}|8\\d{10})$", message = "Invalid phone") String phone);
}