package com.opencu.bookit.adapter.out.persistence.repository;

import com.opencu.bookit.adapter.out.persistence.entity.BookingEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface BookingStatsRepository extends JpaRepository<BookingEntity, Long> {

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