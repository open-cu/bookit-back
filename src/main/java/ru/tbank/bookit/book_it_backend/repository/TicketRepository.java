package ru.tbank.bookit.book_it_backend.repository;

import org.springframework.data.repository.CrudRepository;
import ru.tbank.bookit.book_it_backend.model.Ticket;

import java.util.UUID;

public interface TicketRepository extends CrudRepository<Ticket, UUID> {
}
