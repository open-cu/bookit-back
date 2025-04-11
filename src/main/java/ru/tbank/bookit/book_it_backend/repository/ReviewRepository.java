package ru.tbank.bookit.book_it_backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.tbank.bookit.book_it_backend.model.Review;

import java.util.UUID;

public interface ReviewRepository extends JpaRepository<Review, UUID> {
}
