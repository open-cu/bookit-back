package com.opencu.bookit.initializers;

import com.opencu.bookit.adapter.out.persistence.entity.AreaEntity;
import com.opencu.bookit.adapter.out.persistence.entity.EventEntity;
import com.opencu.bookit.adapter.out.persistence.repository.AreaRepository;
import com.opencu.bookit.adapter.out.persistence.repository.EventRepository;
import com.opencu.bookit.adapter.out.persistence.repository.UserRepository;
import com.opencu.bookit.domain.model.contentcategory.ThemeTags;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Component
@Order(6)
public class EventDataInitializer {

    private static final Logger log = LoggerFactory.getLogger(EventDataInitializer.class);
    private final AreaRepository areaRepository;
    private final UserRepository userRepository;

    @Autowired
    public EventDataInitializer(AreaRepository areaRepository, UserRepository userRepository) {
        this.areaRepository = areaRepository;
        this.userRepository = userRepository;
    }

    @Bean
    ApplicationRunner initEvent(EventRepository eventRepository) {
        return args -> {
            if (eventRepository.count() == 0) {
                LocalDateTime date = LocalDateTime.now().plusDays(2);
                LocalDateTime tommorow = LocalDateTime.now().plusDays(1);

                List<EventEntity> events = List.of(
                        buildEvent(
                                "Ночь презентаций стартапов",
                                "Возможность для стартапов представить свои идеи инвесторам.",
                                Set.of(ThemeTags.IT, ThemeTags.TECHNOLOGY),
                                tommorow.withHour(10).withMinute(0).withSecond(0).withNano(0),
                                30,
                                "arch.png",
                                areaRepository.findAll().getFirst()

                        ),
                        buildEvent(
                                "Мастерская искусственного интеллекта",
                                "Практический семинар по созданию приложений на базе искусственного интеллекта.",
                                Set.of(ThemeTags.IT, ThemeTags.SCIENCE),
                                date.withHour(18).withMinute(0).withSecond(0).withNano(0),
                                0,
                                "arch.png",
                                areaRepository.findAll().getFirst()
                        ),
                        buildEvent(
                                "Мастерская искусственного интеллекта возвращается",
                                "Возможность для стартапов представить свои идеи инвесторам.",
                                Set.of(ThemeTags.IT, ThemeTags.TECHNOLOGY),
                                date.withHour(20).withMinute(0).withSecond(0).withNano(0),
                                30,
                                "arch.png",
                                areaRepository.findAll().getLast()
                        )
                );
                events.getFirst().getUsers().add(userRepository.findAll().getFirst());
                events.getLast().getUsers().add(userRepository.findAll().getFirst());

                eventRepository.saveAll(events);
                log.info("Initial events created successfully");
            }
        };
    }

    private EventEntity buildEvent(String name, String description, Set<ThemeTags> tags, LocalDateTime startTime, int places, String key, AreaEntity area) {
        EventEntity event = new EventEntity();
        event.setName(name);
        event.setDescription(description);
        event.setTags(tags);
        event.setStartTime(startTime);
        event.setKeys(List.of(key));
        event.setAvailable_places(places);
        event.setEndTime(startTime.plusHours(1));
        event.setFormats(Set.of());
        event.setTimes(Set.of());
        event.setParticipationFormats(Set.of());
        event.setArea(area);
        return event;
    }
}