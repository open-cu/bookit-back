package ru.tbank.bookit.book_it_backend.service;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.tbank.bookit.book_it_backend.model.User;
import ru.tbank.bookit.book_it_backend.repository.UserRepository;

import java.util.Optional;
import java.util.UUID;

@Service
public class UserService {
    UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public Optional<User> findById(UUID id) {
        return userRepository.findById(id);
    }
    public UUID getTestUserId() {
        return userRepository.findByName("Alice Johnson").getId();
    }

    public User getCurrentUser() {
        String tgIdString = SecurityContextHolder.getContext().getAuthentication().getName();
        Long tgId = Long.valueOf(tgIdString);
        return userRepository.findByTgId(tgId)
                .orElseThrow(() -> new RuntimeException("Пользователь не найден (tgId): " + tgIdString));
    }

    public Optional<User> getUserById(UUID id) {
        return userRepository.findById(id);
    }

    @Transactional
    public User updateProfile(String firstName, String lastName, String email, String phone) {
        User user = getCurrentUser();
        if (firstName != null) user.setFirstName(firstName);
        if (lastName != null) user.setLastName(lastName);
        if (email != null) user.setEmail(email);
        if (phone != null) user.setPhone(phone);
        return userRepository.save(user);
    }
}
