package ru.tbank.bookit.book_it_backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.tbank.bookit.book_it_backend.model.Booking;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    @Query("SELECT b FROM Booking b WHERE b.userId = :userId")
    Booking findByUserId(@Param("userId") Long userId);

    @Query("SELECT b FROM Booking b WHERE b.startTime BETWEEN ?1 AND DATEADD(day, 1, ?1)")
    List<Booking> findByDate(LocalDate date);

    @Query("SELECT b FROM Booking b WHERE ?1 BETWEEN b.startTime and b.endTime")
    List<Booking> findByDatetime(LocalDateTime date);

    @Query("SELECT b FROM Booking b WHERE b.startTime BETWEEN ?1 AND DATEADD(day, 1, ?1) AND b.areaId = :areaId")
    List<Booking> findByDateAndArea(LocalDate date, @Param("areaId") Long areaId);

    @Query("SELECT b FROM Booking b WHERE b.userId = :userId AND b.startTime < :now AND b.endTime > :now")
    List<Booking> findCurrentBookingsByUser(@Param("userId") Long userId, @Param("now") LocalDateTime now);

    @Query("SELECT b FROM Booking b WHERE b.userId = :userId AND b.startTime > :now")
    List<Booking> findFutureBookingsByUser(@Param("userId") Long userId, @Param("now") LocalDateTime now);

    @Query("SELECT b FROM Booking b WHERE b.userId = :userId AND b.endTime < :now")
    List<Booking> findPastBookingsByUser(@Param("userId") Long userId, @Param("now") LocalDateTime now);
}