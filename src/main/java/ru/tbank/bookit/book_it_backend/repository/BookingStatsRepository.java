package ru.tbank.bookit.book_it_backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.tbank.bookit.book_it_backend.model.Booking;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingStatsRepository extends JpaRepository<Booking, Long> {

    @Query(value = """
        SELECT 
            date_trunc('day', b.start_time)::date AS date,
            a.name AS area_name,
            COUNT(*) AS total_bookings
        FROM bookings b
        JOIN areas a ON b.area_id = a.id
        WHERE b.start_time BETWEEN :start AND :end
        GROUP BY date_trunc('day', b.start_time)::date, a.name
        ORDER BY date
        """, nativeQuery = true)
    List<Object[]> findBookingStatsBetweenDates(
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end);
}