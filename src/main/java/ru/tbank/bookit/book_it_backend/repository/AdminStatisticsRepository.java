package ru.tbank.bookit.book_it_backend.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import ru.tbank.bookit.book_it_backend.model.Booking;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface AdminStatisticsRepository extends CrudRepository<Booking, UUID> {

    @Query(value = """
        SELECT 
            EXTRACT(EPOCH FROM AVG(end_time - start_time))/60 AS avg_minutes
        FROM bookings
        WHERE start_time BETWEEN :start AND :end
        AND status = 'COMPLETED'
        """, nativeQuery = true)
    Double findAverageDuration(@Param("start") LocalDateTime start,
                               @Param("end") LocalDateTime end);

    @Query(value = """
        SELECT 
            a.name AS area_name,
            EXTRACT(EPOCH FROM AVG(b.end_time - b.start_time))/60 AS avg_minutes
        FROM bookings b
        JOIN areas a ON b.area_id = a.id
        WHERE b.start_time BETWEEN :start AND :end
        AND b.status = 'COMPLETED'
        GROUP BY a.name
        """, nativeQuery = true)
    List<Object[]> findAverageDurationByArea(@Param("start") LocalDateTime start,
                                             @Param("end") LocalDateTime end);

    @Query(value = """
        SELECT 
            u.id,
            u.full_name,
            COUNT(b.id) AS bookings_count,
            MAX(b.start_time) AS last_booking
        FROM users u
        JOIN bookings b ON u.id = b.user_id
        WHERE b.start_time BETWEEN :start AND :end
        GROUP BY u.id
        ORDER BY bookings_count DESC
        LIMIT :limit
        """, nativeQuery = true)
    List<Object[]> findTopUsersByBookings(@Param("start") LocalDateTime start,
                                          @Param("end") LocalDateTime end,
                                          @Param("limit") int limit);
}