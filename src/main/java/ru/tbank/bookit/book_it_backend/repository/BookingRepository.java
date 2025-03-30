package ru.tbank.bookit.book_it_backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.tbank.bookit.book_it_backend.model.Booking;


public interface BookingRepository extends JpaRepository<Booking, Long> {
}
