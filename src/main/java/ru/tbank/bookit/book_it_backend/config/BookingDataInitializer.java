package ru.tbank.bookit.book_it_backend.config;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import ru.tbank.bookit.book_it_backend.model.Area;
import ru.tbank.bookit.book_it_backend.model.Booking;
import ru.tbank.bookit.book_it_backend.model.BookingStatus;
import ru.tbank.bookit.book_it_backend.model.User;
import ru.tbank.bookit.book_it_backend.repository.AreaRepository;
import ru.tbank.bookit.book_it_backend.repository.BookingRepository;
import ru.tbank.bookit.book_it_backend.repository.UserRepository;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.LocalDate;
import java.time.Month;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Component
@Order(3)

public class BookingDataInitializer implements ApplicationRunner {
    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final AreaRepository areaRepository;

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
        List<User> users = userRepository.findAll();
        List<Area> areas = areaRepository.findAll();

        if (users.isEmpty() || areas.isEmpty()) {
            System.out.println("Users or Areas not initialized yet. Skipping Booking init.");
            return;
        }

        Booking booking1 = new Booking();
        booking1.setUser(users.getFirst());
        booking1.setArea(areas.getFirst());
        booking1.setStartTime(LocalDateTime.of(LocalDate.of(2025, 4, 14), LocalTime.of(10, 0)));
        booking1.setEndTime(LocalDateTime.of(LocalDate.of(2025, 4, 14), LocalTime.of(12, 0)));
        booking1.setQuantity(1);
        booking1.setStatus(BookingStatus.CONFIRMED);
        booking1.setCreatedAt(LocalDateTime.of(2025, 4, 3, 22, 37, 35, 996095700));
        bookingRepository.save(booking1);

        Booking booking2 = new Booking();
        booking2.setUser(users.get(1));
        booking2.setArea(areas.get(1));
        booking2.setStartTime(LocalDateTime.of(LocalDate.of(2025, 4, 14), LocalTime.of(12, 0)));
        booking2.setEndTime(LocalDateTime.of(LocalDate.of(2025, 4, 14), LocalTime.of(13, 0)));
        booking2.setQuantity(1);
        booking2.setStatus(BookingStatus.CONFIRMED);
        booking2.setCreatedAt(LocalDateTime.of(2025, 4, 3, 22, 39, 25, 746173300));
        bookingRepository.save(booking2);


        Booking booking3 = new Booking();
        booking3.setUser(users.getLast());
        booking3.setArea(areas.getLast());
        booking3.setStartTime(LocalDateTime.of(LocalDate.of(2025, 4, 14), LocalTime.of(8, 0)));
        booking3.setEndTime(LocalDateTime.of(LocalDate.of(2025, 4, 14), LocalTime.of(21, 0)));
        booking3.setQuantity(1);
        booking3.setStatus(BookingStatus.CONFIRMED);
        booking3.setCreatedAt(LocalDateTime.of(2025, 4, 3, 22, 39, 25, 746173300));
        bookingRepository.save(booking3);

        Booking booking4 = new Booking();
        booking4.setUser(userRepository.findByName("Alice Johnson"));
        booking4.setArea(areas.getFirst());
        booking4.setStartTime(LocalDateTime.now().truncatedTo(ChronoUnit.HOURS).minusHours(1));
        booking4.setEndTime(LocalDateTime.now().truncatedTo(ChronoUnit.HOURS).plusHours(1));
        booking4.setQuantity(1);
        booking4.setStatus(BookingStatus.CONFIRMED);
        booking4.setCreatedAt(LocalDateTime.of(2025, 4, 3, 22, 39, 25, 746173300));
        bookingRepository.save(booking4);

        Booking booking5 = new Booking();
        booking5.setUser(userRepository.findByName("Alice Johnson"));
        booking5.setArea(areas.getLast());
        booking5.setStartTime(LocalDateTime.of(2025, Month.FEBRUARY, 14, 12, 0));
        booking5.setEndTime(LocalDateTime.of(2025, Month.FEBRUARY, 14, 14, 0));
        booking5.setQuantity(1);
        booking5.setStatus(BookingStatus.COMPLETED);
        booking5.setCreatedAt(LocalDateTime.of(2025, 1, 20, 22, 39, 25, 746173300));
        bookingRepository.save(booking5);

        Booking booking6 = new Booking();
        booking6.setUser(userRepository.findByName("Alice Johnson"));
        booking6.setArea(areas.getLast());
        booking6.setStartTime(LocalDateTime.of(2025, Month.APRIL, 23, 16, 0));
        booking6.setEndTime(LocalDateTime.of(2025, Month.APRIL, 23, 17, 0));
        booking6.setQuantity(1);
        booking6.setStatus(BookingStatus.COMPLETED);
        booking6.setCreatedAt(LocalDateTime.of(2025, 1, 20, 22, 39, 25, 746173300));
        bookingRepository.save(booking6);
    }
}
