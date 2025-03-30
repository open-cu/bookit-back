package ru.tbank.bookit.book_it_backend.repository;

import org.springframework.data.repository.CrudRepository;
import ru.tbank.bookit.book_it_backend.model.Tickets;

public interface TicketsRepository extends CrudRepository<Tickets, Integer> {
}
