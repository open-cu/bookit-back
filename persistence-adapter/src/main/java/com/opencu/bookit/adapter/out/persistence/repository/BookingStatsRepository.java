package com.opencu.bookit.adapter.out.persistence.repository;

import com.opencu.bookit.adapter.out.persistence.entity.BookingEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface BookingStatsRepository extends JpaRepository<BookingEntity, UUID> {

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
        date_trunc('day', b.start_time)::date AS date,
        a.name AS area_name,
        COUNT(*) AS total_bookings
    FROM bookings b
    JOIN areas a ON b.area_id = a.id
    WHERE b.start_time BETWEEN :start AND :end
    AND a.name IN :areaNames
    GROUP BY date_trunc('day', b.start_time)::date, a.name
    ORDER BY date
    """, nativeQuery = true)
    List<Object[]> findBookingStatsBetweenDatesAndAreas(
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end,
            @Param("areaNames") List<String> areaNames);

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
    AND a.name IN :areaNames
    GROUP BY day_of_week, a.name, EXTRACT(ISODOW FROM b.start_time)
    ORDER BY EXTRACT(ISODOW FROM b.start_time)
    """, nativeQuery = true)
    List<Object[]> findBookingStatsByDayOfWeekAndAreas(
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end,
            @Param("areaNames") List<String> areaNames);

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

    @Query(value = """
    SELECT
        a.name AS area_name,
        COUNT(*) AS total_bookings,
        SUM(CASE WHEN b.status = 'CANCELED' THEN 1 ELSE 0 END) AS cancelled_bookings
    FROM bookings b
    JOIN areas a ON b.area_id = a.id
    WHERE b.start_time BETWEEN :start AND :end
    AND a.name IN :areaNames
    GROUP BY a.name
    ORDER BY cancelled_bookings DESC
    """, nativeQuery = true)
    List<Object[]> findCancellationStatsByAreaAndNames(
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end,
            @Param("areaNames") List<String> areaNames);

    @Query(value = """
    SELECT
        h.hours AS hour_of_day,
        COUNT(b.id) AS bookings_count
    FROM (
        SELECT 8 AS hours UNION ALL SELECT 9 UNION ALL SELECT 10 UNION ALL
        SELECT 11 UNION ALL SELECT 12 UNION ALL SELECT 13 UNION ALL
        SELECT 14 UNION ALL SELECT 15 UNION ALL SELECT 16 UNION ALL
        SELECT 17 UNION ALL SELECT 18 UNION ALL SELECT 19 UNION ALL SELECT 20
    ) h
    LEFT JOIN bookings b ON
        h.hours >= EXTRACT(HOUR FROM b.start_time)
        AND h.hours < EXTRACT(HOUR FROM b.end_time)
        AND b.start_time BETWEEN :start AND :end
    GROUP BY h.hours
    ORDER BY h.hours
    """, nativeQuery = true)
    List<Object[]> findBusiestHoursByArea(
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end);

    @Query(value = """
    SELECT
        h.hours AS hour_of_day,
        COUNT(b.id) AS bookings_count
    FROM (
        SELECT 8 AS hours UNION ALL SELECT 9 UNION ALL SELECT 10 UNION ALL
        SELECT 11 UNION ALL SELECT 12 UNION ALL SELECT 13 UNION ALL
        SELECT 14 UNION ALL SELECT 15 UNION ALL SELECT 16 UNION ALL
        SELECT 17 UNION ALL SELECT 18 UNION ALL SELECT 19 UNION ALL SELECT 20
    ) h
    LEFT JOIN bookings b ON
        h.hours >= EXTRACT(HOUR FROM b.start_time)
        AND h.hours < EXTRACT(HOUR FROM b.end_time)
        AND b.start_time BETWEEN :start AND :end
        AND b.area_id IN (
           SELECT id FROM areas WHERE name IN :areaNames
       )
    GROUP BY h.hours
    ORDER BY h.hours
    """, nativeQuery = true)
    List<Object[]> findBusiestHoursByAreaAndNames(
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end,
            @Param("areaNames") List<String> areaNames);

    @Query(value = """
        WITH event_pairs AS (
            SELECT
                e1.id AS event_id1,
                e1.name AS event_name1,
                e2.id AS event_id2,
                e2.name AS event_name2
            FROM
                events e1
            CROSS JOIN
                events e2
            WHERE
                e1.id <> e2.id  -- Исключаем пару события с самим собой
        ),
        
        event_user_counts AS (
            SELECT
                event_id,
                COUNT(DISTINCT user_id) AS total_users
            FROM
                event_users
            GROUP BY
                event_id
        ),
        
        common_users AS (
            SELECT
                ep.event_id1,
                ep.event_name1,
                ep.event_id2,
                ep.event_name2,
                COUNT(DISTINCT eu1.user_id) AS common_users_count
            FROM
                event_pairs ep
            LEFT JOIN
                event_users eu1 ON ep.event_id1 = eu1.event_id
            LEFT JOIN
                event_users eu2 ON ep.event_id2 = eu2.event_id AND eu1.user_id = eu2.user_id
            GROUP BY
                ep.event_id1, ep.event_name1, ep.event_id2, ep.event_name2
        )

        SELECT
            cu.event_id1,
            cu.event_name1,
            cu.event_id2,
            cu.event_name2,
            COALESCE(cu.common_users_count, 0) AS common_users_count,
            COALESCE(euc1.total_users, 0) AS event1_total_users,
            COALESCE(euc2.total_users, 0) AS event2_total_users,
            CASE
                WHEN COALESCE(cu.common_users_count, 0) = 0 THEN 0
                ELSE (cu.common_users_count::FLOAT / GREATEST(euc1.total_users, euc2.total_users)) * 100
            END AS overlap_percentage
        FROM
            common_users cu
        LEFT JOIN
            event_user_counts euc1 ON cu.event_id1 = euc1.event_id
        LEFT JOIN
            event_user_counts euc2 ON cu.event_id2 = euc2.event_id
        ORDER BY
            cu.event_id1, cu.event_id2;
        """, nativeQuery = true)
    List<Object[]> findEventOverlapPercentage();

    @Query(value = """
    WITH event_pairs AS (
        SELECT
            e1.id AS event_id1,
            e1.name AS event_name1,
            e2.id AS event_id2,
            e2.name AS event_name2
        FROM
            events e1
        CROSS JOIN
            events e2
        WHERE
            e1.id = :eventId1 AND e2.id = :eventId2
    ),

    common_users AS (
        SELECT
            ep.event_id1,
            ep.event_name1,
            ep.event_id2,
            ep.event_name2,
            COUNT(DISTINCT eu1.user_id) AS common_users_count
        FROM
            event_pairs ep
        JOIN
            event_users eu1 ON ep.event_id1 = eu1.event_id
        JOIN
            event_users eu2 ON ep.event_id2 = eu2.event_id AND eu1.user_id = eu2.user_id
        GROUP BY
            ep.event_id1, ep.event_name1, ep.event_id2, ep.event_name2
    ),

    event_user_counts AS (
        SELECT
            event_id,
            COUNT(DISTINCT user_id) AS total_users
        FROM
            event_users
        GROUP BY
            event_id
    )

    SELECT
        cu.event_id1,
        cu.event_name1,
        cu.event_id2,
        cu.event_name2,
        cu.common_users_count,
        euc1.total_users AS event1_total_users,
        euc2.total_users AS event2_total_users,
            (cu.common_users_count::FLOAT /
            GREATEST(euc1.total_users, euc2.total_users)) * 100
         AS overlap_percentage
    FROM
        common_users cu
    JOIN
        event_user_counts euc1 ON cu.event_id1 = euc1.event_id
    JOIN
        event_user_counts euc2 ON cu.event_id2 = euc2.event_id
    ORDER BY
        overlap_percentage DESC
    """, nativeQuery = true)
    List<Object[]> findEventOverlapPercentage(@Param("eventId1") UUID eventId1,
                                              @Param("eventId2") UUID eventId2);

    @Query(value = """
    WITH event_pairs AS (
        SELECT
            e1.id AS event_id1,
            e1.name AS event_name1,
            e2.id AS event_id2,
            e2.name AS event_name2
        FROM
            events e1
        CROSS JOIN
            events e2
        WHERE
            e1.id < e2.id
            AND (e1.id = :eventId OR e2.id = :eventId)
    ),

    common_users AS (
        SELECT
            ep.event_id1,
            ep.event_name1,
            ep.event_id2,
            ep.event_name2,
            COUNT(DISTINCT eu1.user_id) AS common_users_count
        FROM
            event_pairs ep
        JOIN
            event_users eu1 ON ep.event_id1 = eu1.event_id
        JOIN
            event_users eu2 ON ep.event_id2 = eu2.event_id AND eu1.user_id = eu2.user_id
        GROUP BY
            ep.event_id1, ep.event_name1, ep.event_id2, ep.event_name2
    ),

    event_user_counts AS (
        SELECT
            event_id,
            COUNT(DISTINCT user_id) AS total_users
        FROM
            event_users
        GROUP BY
            event_id
    )

    SELECT
        cu.event_id1,
        cu.event_name1,
        cu.event_id2,
        cu.event_name2,
        cu.common_users_count,
        euc1.total_users AS event1_total_users,
        euc2.total_users AS event2_total_users,
            (cu.common_users_count::FLOAT /
            GREATEST(euc1.total_users, euc2.total_users)) * 100
         AS overlap_percentage
    FROM
        common_users cu
    JOIN
        event_user_counts euc1 ON cu.event_id1 = euc1.event_id
    JOIN
        event_user_counts euc2 ON cu.event_id2 = euc2.event_id
    ORDER BY
        overlap_percentage DESC
    """, nativeQuery = true)
    List<Object[]> findEventOverlapPercentage(@Param("eventId") UUID eventId);

    @Query(value = """
        SELECT TO_CHAR(created_at, 'YYYY-MM') as created, COUNT(id) as count FROM users
          GROUP BY TO_CHAR(created_at, 'YYYY-MM');
    """, nativeQuery = true)
    List<Object[]> findNewUsersByCreatedAtYearMonth();

}