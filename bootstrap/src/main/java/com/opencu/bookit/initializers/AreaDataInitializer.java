package com.opencu.bookit.initializers;

import com.opencu.bookit.adapter.out.persistence.entity.AreaEntity;
import com.opencu.bookit.adapter.out.persistence.mapper.AreaMapper;
import com.opencu.bookit.adapter.out.persistence.repository.AreaRepository;
import com.opencu.bookit.application.config.BookingConfig;
import com.opencu.bookit.domain.model.area.AreaFeature;
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
            openSpace.setDescription("Большая открытая зона с общими столами и удобными сиденьями.");
            openSpace.setType(AreaType.WORKPLACE);
            openSpace.setCapacity(bookingConfig.getHallMaxCapacity());
            openSpace.setFeatures(List.of(AreaFeature.CHANCELLERY));
            openSpace.setKeys(List.of("arch.png"));
            openSpace.setStatus(AreaStatus.AVAILABLE);

            AreaEntity meetingRoom = new AreaEntity();
            meetingRoom.setName("Meeting Room Alpha");
            meetingRoom.setDescription("Отдельная переговорная комната с проектором и конференц-столом.");
            meetingRoom.setType(AreaType.MEETING_ROOM);
            meetingRoom.setKeys(List.of("arch.png"));
            openSpace.setFeatures(List.of(AreaFeature.CHANCELLERY));
            meetingRoom.setCapacity(10);
            meetingRoom.setStatus(AreaStatus.BOOKED);

            AreaEntity quietZone = new AreaEntity();
            quietZone.setName("Quiet Zone");
            quietZone.setDescription("Выделенное тихое рабочее место для сосредоточенной работы.");
            quietZone.setType(AreaType.MEETING_ROOM);
            quietZone.setKeys(List.of("arch.png"));
            openSpace.setFeatures(List.of(AreaFeature.CHANCELLERY));
            quietZone.setCapacity(10);
            quietZone.setStatus(AreaStatus.AVAILABLE);

            areaRepository.saveAll(List.of(openSpace, meetingRoom, quietZone));
            System.out.println("Initial areas created successfully");
        }
    }
}