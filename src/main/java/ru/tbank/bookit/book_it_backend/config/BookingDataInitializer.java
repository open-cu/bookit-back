package ru.tbank.bookit.book_it_backend.config;

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

        User aliceJohnson = userRepository.findByName("Alice Johnson");

        Booking booking1 = createBooking(aliceJohnson, areas.getFirst(), Month.APRIL, 14);
        Booking booking2 = createBooking(aliceJohnson, areas.getLast(), Month.APRIL, 14);
        Booking booking3 = createBooking(aliceJohnson, areas.get(1), Month.APRIL, 14);


        Booking booking4 = createBooking(aliceJohnson, areas.getFirst(), Month.JANUARY, 23);
        Booking booking5 = createBooking(aliceJohnson, areas.get(1), Month.JANUARY, 23);
        Booking booking6 = createBooking(aliceJohnson, areas.getLast(), Month.JANUARY, 23);

        Booking booking7 = createCurrentBooking(aliceJohnson, areas.getLast());
        Booking booking8 = createCurrentBooking(aliceJohnson, areas.get(1));
        Booking booking9 = createCurrentBooking(aliceJohnson, areas.getFirst());

        bookingRepository.saveAll(List.of(booking1, booking2, booking3, booking4,
                                          booking5, booking6, booking7, booking8, booking9));
    }

    public Booking createCurrentBooking(User user, Area area) {
        Booking booking = new Booking();
        booking.setUser(user);
        booking.setArea(area);
        booking.setStartTime(LocalDateTime.now().truncatedTo(ChronoUnit.HOURS).minusHours(1));
        booking.setEndTime(LocalDateTime.now().truncatedTo(ChronoUnit.HOURS).plusHours(1));
        booking.setQuantity(1);
        booking.setStatus(BookingStatus.CONFIRMED);
        booking.setCreatedAt(LocalDateTime.of(2025, 4, 3, 22, 39, 25, 746173300));
        return booking;
    }

    public Booking createBooking(User user, Area area, Month month, int day) {
        Booking booking = new Booking();
        booking.setUser(user);
        booking.setArea(area);
        booking.setStartTime(LocalDateTime.of(LocalDate.of(2025, month, day), LocalTime.of(10, 0)));
        booking.setEndTime(LocalDateTime.of(LocalDate.of(2025, month, day), LocalTime.of(12, 0)));
        booking.setQuantity(1);
        booking.setStatus(BookingStatus.CONFIRMED);
        booking.setCreatedAt(LocalDateTime.of(2025, 1, 3, 22, 39, 25, 746173300));
        return booking;
    }
}
