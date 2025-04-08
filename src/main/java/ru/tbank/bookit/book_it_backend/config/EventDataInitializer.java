package ru.tbank.bookit.book_it_backend.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.tbank.bookit.book_it_backend.model.Event;
import ru.tbank.bookit.book_it_backend.model.ThemeTags;
import ru.tbank.bookit.book_it_backend.repository.EventRepository;
import ru.tbank.bookit.book_it_backend.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Configuration
public class EventDataInitializer {

    @Bean
    CommandLineRunner initEvent(EventRepository eventRepository, UserRepository userRepository) {
        return args -> {
            if (eventRepository.count() == 0) {
                // Создаем события
                Event pitchNight = new Event();
                pitchNight.setName("Startup Pitch Night");
                pitchNight.setDescription("An opportunity for startups to present their ideas to investors.");
                pitchNight.setTags(Set.of(ThemeTags.IT, ThemeTags.TECHNOLOGY));
                pitchNight.setDate(LocalDateTime.of(2025, 7, 10, 0, 0, 0));
                pitchNight.setAvailable_places(30);

                Event aiWorkshop = new Event();
                aiWorkshop.setName("AI Workshop");
                aiWorkshop.setDescription("Hands-on workshop on building AI-powered applications.");
                aiWorkshop.setTags(Set.of(ThemeTags.IT, ThemeTags.SCIENCE));
                aiWorkshop.setDate(LocalDateTime.of(2025, 8, 20, 0, 0, 0));
                aiWorkshop.setAvailable_places(20);

                UUID user1 = UUID.fromString("ca261095-1578-4aa4-9c8e-1e227d8f9aee");
                UUID user2 = UUID.fromString("5156943e-61fe-4334-8ced-26ac5c3998c0");

                if (userRepository.existsById(user1) && userRepository.existsById(user2)) {
                    aiWorkshop.setUser_list(String.valueOf(List.of(user1, user2)));
                } else {
                    System.out.println("Warning: Some users not found for event participation");
                }

                eventRepository.saveAll(List.of(pitchNight, aiWorkshop));
                System.out.println("Initial events created successfully");
            }
        };
    }
}