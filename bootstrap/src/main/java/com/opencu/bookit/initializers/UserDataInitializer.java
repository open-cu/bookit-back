package com.opencu.bookit.initializers;

import com.opencu.bookit.adapter.out.persistence.entity.RoleEntity;
import com.opencu.bookit.adapter.out.persistence.entity.UserEntity;
import com.opencu.bookit.adapter.out.persistence.repository.RoleRepository;
import com.opencu.bookit.adapter.out.persistence.repository.UserRepository;
import com.opencu.bookit.domain.model.user.UserStatus;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Configuration
@Component
@Order(1)
public class UserDataInitializer implements ApplicationRunner {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    public UserDataInitializer(UserRepository userRepository, RoleRepository roleRepository) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        if (userRepository.count() == 0) {
            UserEntity alice = new UserEntity();
            alice.setTgId(1234567890L);
            alice.setFirstName("Alice");
            alice.setLastName("Johnson");
            alice.setUsername("alicejohnson");
            alice.setEmail("alice@example.com");
            alice.setPasswordHash("$2b$12$abcdefghijklmnopqrstuv");
            alice.setPhone("+79123456789");
            alice.setCreatedAt(LocalDateTime.of(2025, 4, 3, 12, 0));
            alice.setStatus(UserStatus.CREATED);

            UserEntity bob = new UserEntity();
            bob.setTgId(1987654321L);
            bob.setFirstName("Bob");
            bob.setLastName("Smith");
            bob.setUsername("bobsmith");
            bob.setEmail("bob@example.com");
            bob.setPasswordHash("$2b$12$zyxwvutsrqponmlkjihgfedc");
            bob.setPhone("+79219876543");
            bob.setCreatedAt(LocalDateTime.of(2025, 4, 3, 12, 5));
            bob.setStatus(UserStatus.CREATED);

            UserEntity charlie = new UserEntity();
            charlie.setTgId(8987654325L);
            charlie.setFirstName("Charlie Davis");
            charlie.setLastName("Davis");
            charlie.setUsername("charliedavis");
            charlie.setEmail("charlie@example.com");
            charlie.setPasswordHash("$2b$12$1234567890abcdefgijklmn");
            charlie.setPhone("+79219876542");
            charlie.setCreatedAt(LocalDateTime.of(2025, 4, 3, 12, 10));
            charlie.setStatus(UserStatus.BANNED);

            userRepository.saveAll(List.of(alice, bob, charlie));
            System.out.println("Initial users created successfully");
        }

        if (roleRepository.count() == 0) {
            RoleEntity entity1 = new RoleEntity();
            entity1.setName(RoleEntity.RoleName.ROLE_USER);
            roleRepository.save(entity1);

            RoleEntity entity2 = new RoleEntity();
            entity2.setName(RoleEntity.RoleName.ROLE_ADMIN);
            roleRepository.save(entity2);

            RoleEntity entity3 = new RoleEntity();
            entity3.setName(RoleEntity.RoleName.ROLE_SUPERADMIN);
            roleRepository.save(entity3);
        }
    }
}