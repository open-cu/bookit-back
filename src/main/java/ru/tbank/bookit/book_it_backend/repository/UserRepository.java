package ru.tbank.bookit.book_it_backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.tbank.bookit.book_it_backend.model.User;

import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {
}
