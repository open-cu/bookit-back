package ru.tbank.bookit.book_it_backend.config;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import ru.tbank.bookit.book_it_backend.model.Area;
import ru.tbank.bookit.book_it_backend.model.Event;
import ru.tbank.bookit.book_it_backend.model.ThemeTags;
import ru.tbank.bookit.book_it_backend.model.User;
import ru.tbank.bookit.book_it_backend.repository.BookingRepository;
import ru.tbank.bookit.book_it_backend.repository.EventRepository;
import ru.tbank.bookit.book_it_backend.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Component
@Order(4)
public class EventDataInitializer implements ApplicationRunner {
    private final EventRepository eventRepository;
    private final UserRepository userRepository;

    public EventDataInitializer(EventRepository eventRepository, UserRepository userRepository) {
        this.eventRepository = eventRepository;
        this.userRepository = userRepository;
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        if (eventRepository.count() == 0) {

            List<User> users = userRepository.findAll();

            if (users.isEmpty()) {
                System.out.println("Users or Areas not initialized yet. Skipping Booking init.");
                return;
            }
            Event pitchNight = new Event();
            pitchNight.setName("Startup Pitch Night");
            pitchNight.setDescription("An opportunity for startups to present their ideas to investors.");
            pitchNight.setTags(Set.of(ThemeTags.IT, ThemeTags.TECHNOLOGY));
            pitchNight.setDate(LocalDateTime.of(2025, 7, 10, 0, 0, 0));
            pitchNight.setAvailable_places(30);
            pitchNight.setCreatedAt(LocalDateTime.of(2025, 4, 3, 22, 39, 25, 746173300));

            Event aiWorkshop = new Event();
            aiWorkshop.setName("AI Workshop");
            aiWorkshop.setDescription("Hands-on workshop on building AI-powered applications.");
            aiWorkshop.setTags(Set.of(ThemeTags.IT, ThemeTags.SCIENCE));
            aiWorkshop.setDate(LocalDateTime.of(2025, 8, 20, 0, 0, 0));
            aiWorkshop.setAvailable_places(20);
            aiWorkshop.setCreatedAt(LocalDateTime.of(2025, 4, 3, 22, 39, 25, 746173300));

            UUID user1 = users.getFirst().getId();
            UUID user2 = users.getLast().getId();

            if (userRepository.existsById(user1) && userRepository.existsById(user2)) {
                aiWorkshop.setUser_list(List.of(user1, user2));
            } else {
                System.out.println("Warning: Some users not found for event participation");
            }

            eventRepository.saveAll(List.of(pitchNight, aiWorkshop));
            System.out.println("Initial events created successfully");
        }
    }
}