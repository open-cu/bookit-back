package ru.tbank.bookit.book_it_backend.config;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import ru.tbank.bookit.book_it_backend.model.User;
import ru.tbank.bookit.book_it_backend.model.UserStatus;
import ru.tbank.bookit.book_it_backend.repository.UserRepository;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;

@Configuration
@Component
@Order(1)
public class UserDataInitializer implements ApplicationRunner {
    private final UserRepository userRepository;

    public UserDataInitializer(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        if (userRepository.count() == 0) {
            User alice = new User();
            alice.setTg_id(1234567890L);
            alice.setName("Alice Johnson");
            alice.setEmail("alice@example.com");
            alice.setPasswordHash("$2b$12$abcdefghijklmnopqrstuv");
            alice.setPhone(79123456789L);
            alice.setCreatedAt(LocalDateTime.of(2025, 4, 3, 12, 0));
            alice.setStatus(UserStatus.CREATED);

            User bob = new User();
            bob.setTg_id(1987654321L);
            bob.setName("Bob Smith");
            bob.setEmail("bob@example.com");
            bob.setPasswordHash("$2b$12$zyxwvutsrqponmlkjihgfedc");
            bob.setPhone(79219876543L);
            bob.setCreatedAt(LocalDateTime.of(2025, 4, 3, 12, 5));
            bob.setStatus(UserStatus.CREATED);

            User charlie = new User();
            charlie.setTg_id(8987654325L);
            charlie.setName("Charlie Davis");
            charlie.setEmail("charlie@example.com");
            charlie.setPasswordHash("$2b$12$1234567890abcdefgijklmn");
            charlie.setPhone(79219876542L);
            charlie.setCreatedAt(LocalDateTime.of(2025, 4, 3, 12, 10));
            charlie.setStatus(UserStatus.BANNED);

            userRepository.saveAll(List.of(alice, bob, charlie));
            System.out.println("Initial users created successfully");
        }
    }
}