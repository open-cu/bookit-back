package com.opencu.bookit.adapter.out.persistence.adapter;

import com.opencu.bookit.adapter.out.persistence.mapper.ScheduleMapper;
import com.opencu.bookit.adapter.out.persistence.repository.ScheduleRepository;
import com.opencu.bookit.application.port.out.schedule.LoadNonWorkingDaySchedulePort;
import com.opencu.bookit.application.port.out.schedule.SaveSchedulePort;
import com.opencu.bookit.domain.model.schedule.ScheduleModel;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class ScheduleAdapter implements LoadNonWorkingDaySchedulePort, SaveSchedulePort {

    private final ScheduleRepository scheduleRepository;
    private final ScheduleMapper scheduleMapper;

    @Override
    public Optional<ScheduleModel> findByDate(LocalDate date) {
        return scheduleRepository.findByDate(date).map(scheduleMapper::toModel);
    }

    @Override
    public Optional<ScheduleModel> findById(LocalDate date) {
        return scheduleRepository.findById(date).map(scheduleMapper::toModel);
    }

    @Override
    public ScheduleModel save(ScheduleModel schedule) {
        var entity = scheduleMapper.toEntity(schedule);
        var savedEntity = scheduleRepository.save(entity);
        return scheduleMapper.toModel(savedEntity);
    }
}

