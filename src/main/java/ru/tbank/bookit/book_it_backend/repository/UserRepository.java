package ru.tbank.bookit.book_it_backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.tbank.bookit.book_it_backend.model.User;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {
    //поиск по имени
    @Query("SELECT u FROM User u WHERE u.name = ?1")
    User findByName(String name);

    Optional<User> findByUsername(String username);

    Boolean existsByUsername(String username);

    Boolean existsByEmail(String email);

    Optional<User> findByEmail(String email);

    Optional<User> findByPhone(long phone);

    Optional<User> findByTgId(long tgId);
}
