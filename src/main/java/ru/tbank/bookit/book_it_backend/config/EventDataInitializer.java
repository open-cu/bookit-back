package ru.tbank.bookit.book_it_backend.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.tbank.bookit.book_it_backend.model.Event;
import ru.tbank.bookit.book_it_backend.model.ThemeTags;
import ru.tbank.bookit.book_it_backend.model.User;
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
                aiWorkshop.setAvailable_places(0);

                Event aiWorkshop2 = new Event();
                aiWorkshop2.setName("AI Workshop is back");
                aiWorkshop2.setDescription("An opportunity for startups to present their ideas to investors.");
                aiWorkshop2.setTags(Set.of(ThemeTags.IT, ThemeTags.TECHNOLOGY));
                aiWorkshop2.setDate(LocalDateTime.of(2025, 9, 10, 0, 0, 0));
                aiWorkshop2.setAvailable_places(30);

                User user1 = userRepository.findByName("Alice Johnson");
                aiWorkshop2.getUsers().add(user1);
                eventRepository.save(aiWorkshop2);

                eventRepository.saveAll(List.of(pitchNight, aiWorkshop, aiWorkshop2));
                System.out.println("Initial events created successfully");
            }
        };
    }
}