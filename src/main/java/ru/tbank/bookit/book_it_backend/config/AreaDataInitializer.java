package ru.tbank.bookit.book_it_backend.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.tbank.bookit.book_it_backend.model.Area;
import ru.tbank.bookit.book_it_backend.model.AreaFeature;
import ru.tbank.bookit.book_it_backend.model.AreaStatus;
import ru.tbank.bookit.book_it_backend.model.AreaType;
import ru.tbank.bookit.book_it_backend.repository.AreaRepository;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Configuration
public class AreaDataInitializer {

    @Bean
    CommandLineRunner initArea(AreaRepository areaRepository) {
        return args -> {
            if (areaRepository.count() == 0) {
                Area openSpace = new Area();
                openSpace.setName("Open Space");
                openSpace.setDescription("Large open area with shared desks and comfortable seating.");
                openSpace.setType(AreaType.WORKPLACE);
                openSpace.setCapacity(30);
                openSpace.setStatus(AreaStatus.AVAILABLE);

                Area meetingRoom = new Area();
                meetingRoom.setName("Meeting Room Alpha");
                meetingRoom.setDescription("Private meeting room with a projector and conference table.");
                meetingRoom.setType(AreaType.MEETING_ROOM);
                meetingRoom.setCapacity(10);
                meetingRoom.setStatus(AreaStatus.BOOKED);

                Area quietZone = new Area();
                quietZone.setName("Quiet Zone");
                quietZone.setDescription("Dedicated silent workspace for focused work.");
                quietZone.setType(AreaType.MEETING_ROOM);
                quietZone.setCapacity(10);
                quietZone.setStatus(AreaStatus.AVAILABLE);

                areaRepository.saveAll(List.of(openSpace, meetingRoom, quietZone));
                System.out.println("Initial areas created successfully");
            }
        };
    }
}