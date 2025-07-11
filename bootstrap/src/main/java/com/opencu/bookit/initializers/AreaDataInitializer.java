package com.opencu.bookit.initializers;

import com.opencu.bookit.adapter.out.persistence.entity.AreaEntity;
import com.opencu.bookit.adapter.out.persistence.mapper.AreaMapper;
import com.opencu.bookit.adapter.out.persistence.repository.AreaRepository;
import com.opencu.bookit.application.config.BookingConfig;
import com.opencu.bookit.domain.model.area.AreaModel;
import com.opencu.bookit.domain.model.area.AreaStatus;
import com.opencu.bookit.domain.model.area.AreaType;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.List;

@Configuration
@Component
@Order(2)
public class AreaDataInitializer implements ApplicationRunner {
    private final AreaRepository areaRepository;
    private final BookingConfig bookingConfig;

    public AreaDataInitializer(AreaRepository areaRepository, BookingConfig bookingConfig) {
        this.areaRepository = areaRepository;
        this.bookingConfig = bookingConfig;
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        if (areaRepository.count() == 0) {
            AreaEntity openSpace = new AreaEntity();
            openSpace.setName("Open Space");
            openSpace.setDescription("Large open area with shared desks and comfortable seating.");
            openSpace.setType(AreaType.WORKPLACE);
            openSpace.setCapacity(bookingConfig.getHallMaxCapacity());
            openSpace.setStatus(AreaStatus.AVAILABLE);

            AreaEntity meetingRoom = new AreaEntity();
            meetingRoom.setName("Meeting Room Alpha");
            meetingRoom.setDescription("Private meeting room with a projector and conference table.");
            meetingRoom.setType(AreaType.MEETING_ROOM);
            meetingRoom.setCapacity(10);
            meetingRoom.setStatus(AreaStatus.BOOKED);

            AreaEntity quietZone = new AreaEntity();
            quietZone.setName("Quiet Zone");
            quietZone.setDescription("Dedicated silent workspace for focused work.");
            quietZone.setType(AreaType.MEETING_ROOM);
            quietZone.setCapacity(10);
            quietZone.setStatus(AreaStatus.AVAILABLE);

            areaRepository.saveAll(List.of(openSpace, meetingRoom, quietZone));
            System.out.println("Initial areas created successfully");
        }
    }
}