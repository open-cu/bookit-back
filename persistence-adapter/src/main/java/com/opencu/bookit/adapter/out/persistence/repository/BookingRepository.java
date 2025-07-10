package com.opencu.bookit.adapter.out.persistence.repository;


import com.opencu.bookit.adapter.out.persistence.entity.BookingEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface BookingRepository extends JpaRepository<BookingEntity, UUID> {
    @Query("SELECT b FROM BookingEntity b WHERE b.area.id = :userId")
    Optional<BookingEntity> findByUserId(@Param("userId") UUID userId);

    @Query("SELECT b FROM BookingEntity b WHERE b.area.id = :areaId")
    List<BookingEntity> findByAreaId(@Param("areaId") UUID areaId);

    @Query("SELECT b FROM BookingEntity b WHERE CAST(b.startTime AS DATE) = ?1")
    List<BookingEntity> findByDate(LocalDate date);

    @Query("SELECT b FROM BookingEntity b WHERE b.startTime = ?1")
    List<BookingEntity> findByStartDatetime(LocalDateTime date);

    @Query("SELECT b FROM BookingEntity b WHERE b.startTime <= ?1 AND ?1 < b.endTime")
    List<BookingEntity> findByDatetime(LocalDateTime date);

    @Query("SELECT b FROM BookingEntity b WHERE CAST(b.startTime AS DATE) = ?1 AND b.area.id = :areaId")
    List<BookingEntity> findByDateAndArea(LocalDate date, @Param("areaId") UUID areaId);

    @Query("SELECT b FROM BookingEntity b WHERE b.user.id = :userId AND b.startTime < :now AND b.endTime > :now")
    List<BookingEntity> findCurrentBookingsByUser(@Param("userId") UUID userId, @Param("now") LocalDateTime now);

    @Query("SELECT b FROM BookingEntity b WHERE b.user.id = :userId AND b.startTime > :now")
    List<BookingEntity> findFutureBookingsByUser(@Param("userId") UUID userId, @Param("now") LocalDateTime now);

    @Query("SELECT b FROM BookingEntity b WHERE b.user.id = :userId AND b.endTime < :now")
    List<BookingEntity> findPastBookingsByUser(@Param("userId") UUID userId, @Param("now") LocalDateTime now);
}