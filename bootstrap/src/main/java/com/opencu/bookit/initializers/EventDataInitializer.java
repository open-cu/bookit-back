package com.opencu.bookit.initializers;

import com.opencu.bookit.adapter.out.persistence.entity.EventEntity;
import com.opencu.bookit.adapter.out.persistence.repository.EventRepository;
import com.opencu.bookit.adapter.out.persistence.repository.UserRepository;
import com.opencu.bookit.domain.model.contentcategory.ThemeTags;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Configuration
public class EventDataInitializer {

    @Bean
    CommandLineRunner initEvent(EventRepository eventRepository, UserRepository userRepository) {
        return args -> {
            if (eventRepository.count() == 0) {
                // Создаем события
                EventEntity pitchNight = new EventEntity();
                pitchNight.setName("Ночь презентаций стартапов");
                pitchNight.setDescription("Возможность для стартапов представить свои идеи инвесторам.");
                pitchNight.setTags(Set.of(ThemeTags.IT, ThemeTags.TECHNOLOGY));
                pitchNight.setDate(LocalDateTime.of(2025, 7, 28, 16, 0, 0));
                pitchNight.setKeys(List.of("arch.png"));
                pitchNight.setAvailable_places(30);

                EventEntity aiWorkshop = new EventEntity();
                aiWorkshop.setName("Мастерская искусственного интеллекта");
                aiWorkshop.setDescription("Практический семинар по созданию приложений на базе искусственного интеллекта.");
                aiWorkshop.setTags(Set.of(ThemeTags.IT, ThemeTags.SCIENCE));
                aiWorkshop.setDate(LocalDateTime.of(2025, 7, 28, 18, 0, 0));
                aiWorkshop.setKeys(List.of("arch.png"));
                aiWorkshop.setAvailable_places(0);

                EventEntity aiWorkshop2 = new EventEntity();
                aiWorkshop2.setName("Мастерская искусственного интеллекта возвращается");
                aiWorkshop2.setDescription("Возможность для стартапов представить свои идеи инвесторам.");
                aiWorkshop2.setTags(Set.of(ThemeTags.IT, ThemeTags.TECHNOLOGY));
                aiWorkshop2.setDate(LocalDateTime.of(2025, 7, 28, 20, 0, 0));
                aiWorkshop2.setKeys(List.of("arch.png"));
                aiWorkshop2.setAvailable_places(30);

                eventRepository.saveAll(List.of(pitchNight, aiWorkshop, aiWorkshop2));
                System.out.println("Initial events created successfully");
            }
        };
    }
}