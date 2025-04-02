package ru.tbank.bookit.book_it_backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.tbank.bookit.book_it_backend.model.Booking;

import java.time.LocalDate;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    @Query("SELECT b FROM Booking b WHERE b.userId = :userId")
    Booking findBookingById(@Param("userId") Long userId);

    @Query("SELECT b FROM Booking b")
    List<Booking> findBookings();

    @Query("SELECT b FROM Booking b WHERE b.startTime BETWEEN ?1 AND DATEADD(day, 1, ?1)")
    List<Booking> findBookingsInDate(LocalDate date);

    @Query("SELECT b FROM Booking b WHERE b.startTime BETWEEN ?1 AND DATEADD(day, 1, ?1) AND b.areaId = :areaId")
    List<Booking> findBookingsInDateAndArea(LocalDate date, @Param("areaId") Long areaId);
}
