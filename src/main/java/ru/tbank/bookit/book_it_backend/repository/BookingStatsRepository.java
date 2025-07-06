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

    @Query(value = """
    SELECT 
        CASE 
            WHEN EXTRACT(ISODOW FROM b.start_time) = 1 THEN 'Понедельник'
            WHEN EXTRACT(ISODOW FROM b.start_time) = 2 THEN 'Вторник'
            WHEN EXTRACT(ISODOW FROM b.start_time) = 3 THEN 'Среда'
            WHEN EXTRACT(ISODOW FROM b.start_time) = 4 THEN 'Четверг'
            WHEN EXTRACT(ISODOW FROM b.start_time) = 5 THEN 'Пятница'
            WHEN EXTRACT(ISODOW FROM b.start_time) = 6 THEN 'Суббота'
            WHEN EXTRACT(ISODOW FROM b.start_time) = 7 THEN 'Воскресенье'
        END AS day_of_week,
        COUNT(*) AS total_bookings,
        a.name AS area_name
    FROM bookings b
    JOIN areas a ON b.area_id = a.id
    WHERE b.start_time BETWEEN :start AND :end
    GROUP BY day_of_week, a.name, EXTRACT(ISODOW FROM b.start_time)
    ORDER BY EXTRACT(ISODOW FROM b.start_time)
    """, nativeQuery = true)
    List<Object[]> findBookingStatsByDayOfWeek(
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end);

    @Query(value = """
    SELECT 
        a.name AS area_name,
        COUNT(*) AS total_bookings,
        SUM(CASE WHEN b.status = 'CANCELED' THEN 1 ELSE 0 END) AS cancelled_bookings
    FROM bookings b
    JOIN areas a ON b.area_id = a.id
    WHERE b.start_time BETWEEN :start AND :end
    GROUP BY a.name
    ORDER BY cancelled_bookings DESC
    """, nativeQuery = true)
    List<Object[]> findCancellationStatsByArea(
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end);
}