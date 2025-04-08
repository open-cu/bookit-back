package ru.tbank.bookit.book_it_backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.tbank.bookit.book_it_backend.model.User;

import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {
    //поиск по имени
    @Query("SELECT u FROM User u WHERE u.name = ?1")
    User findByName(String name);
}
