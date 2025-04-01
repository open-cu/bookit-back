package ru.tbank.bookit.book_it_backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.tbank.bookit.book_it_backend.model.Event;

public interface EventRepository extends JpaRepository<Event, Long> {
}
