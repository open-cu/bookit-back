package com.opencu.bookit.initializers;

import com.opencu.bookit.adapter.out.persistence.entity.AreaEntity;
import com.opencu.bookit.adapter.out.persistence.entity.BookingEntity;
import com.opencu.bookit.adapter.out.persistence.entity.UserEntity;
import com.opencu.bookit.adapter.out.persistence.repository.AreaRepository;
import com.opencu.bookit.adapter.out.persistence.repository.BookingRepository;
import com.opencu.bookit.adapter.out.persistence.repository.UserRepository;
import com.opencu.bookit.application.config.BookingConfig;
import com.opencu.bookit.application.service.booking.BookingService;
import com.opencu.bookit.domain.model.booking.BookingStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.time.*;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

@Component
@Order(3)
public class BookingDataInitializer implements ApplicationRunner {
    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final AreaRepository areaRepository;

    @Value("${booking.zone-id}")
    private ZoneId zoneId;

    @Autowired
    public BookingDataInitializer(BookingRepository bookingRepository,
                                  UserRepository userRepository,
                                  AreaRepository areaRepository) {
        this.bookingRepository = bookingRepository;
        this.userRepository = userRepository;
        this.areaRepository = areaRepository;
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        List<UserEntity> users = userRepository.findAll();
        List<AreaEntity> areas = areaRepository.findAll();

        if (users.isEmpty() || areas.isEmpty()) {
            System.out.println("Users or Areas not initialized yet. Skipping Booking init.");
            return;
        }

        Optional<UserEntity> aliceOpt = userRepository.findByFirstNameAndLastName("Alice", "Johnson");
        if (aliceOpt.isEmpty()) {
            System.out.println("User Alice Johnson not found! Skipping Booking init.");
            return;
        }
        UserEntity aliceJohnson = aliceOpt.get();

        BookingEntity booking1 = createBooking(aliceJohnson, areas.getFirst(), Month.MAY, 25);
        BookingEntity booking2 = createBooking(aliceJohnson, areas.getLast(), Month.MAY, 25);
        BookingEntity booking3 = createBooking(aliceJohnson, areas.get(1), Month.MAY, 25);

        BookingEntity booking4 = createBooking(aliceJohnson, areas.getFirst(), Month.JANUARY, 23);
        BookingEntity booking5 = createBooking(aliceJohnson, areas.get(1), Month.JANUARY, 23);
        BookingEntity booking6 = createBooking(aliceJohnson, areas.getLast(), Month.JANUARY, 23);

        BookingEntity booking7 = createCurrentBooking(aliceJohnson, areas.getLast());
        BookingEntity booking8 = createCurrentBooking(aliceJohnson, areas.get(1));
        BookingEntity booking9 = createCurrentBooking(aliceJohnson, areas.getFirst());
        booking9.setStatus(BookingStatus.CANCELED);

        bookingRepository.saveAll(List.of(booking1, booking2, booking3, booking4,
                booking5, booking6, booking7, booking8, booking9));
    }

    public BookingEntity createCurrentBooking(UserEntity user, AreaEntity area) {
        BookingEntity booking = new BookingEntity();
        booking.setUserEntity(user);
        booking.setAreaEntity(area);
        booking.setStartTime(LocalDateTime.now(zoneId).truncatedTo(ChronoUnit.HOURS).minusHours(1));
        booking.setEndTime(LocalDateTime.now(zoneId).truncatedTo(ChronoUnit.HOURS).plusHours(1));
        booking.setQuantity(1);
        booking.setStatus(BookingStatus.CONFIRMED);
        booking.setCreatedAt(LocalDateTime.of(2025, 4, 3, 22, 39, 25, 746173300));
        return booking;
    }

    public BookingEntity createBooking(UserEntity user, AreaEntity area, Month month, int day) {
        BookingEntity booking = new BookingEntity();
        booking.setUserEntity(user);
        booking.setAreaEntity(area);
        booking.setStartTime(LocalDateTime.of(LocalDate.of(2025, month, day), LocalTime.of(16, 0)));
        booking.setEndTime(LocalDateTime.of(LocalDate.of(2025, month, day), LocalTime.of(18, 0)));
        booking.setQuantity(1);
        booking.setStatus(BookingStatus.CONFIRMED);
        booking.setCreatedAt(LocalDateTime.of(2025, 1, 3, 22, 39, 25, 746173300));
        return booking;
    }
}