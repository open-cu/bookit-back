package ru.tbank.bookit.book_it_backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.tbank.bookit.book_it_backend.model.Reviews;

public interface ReviewsRepository extends JpaRepository<Reviews, Long> {
}
