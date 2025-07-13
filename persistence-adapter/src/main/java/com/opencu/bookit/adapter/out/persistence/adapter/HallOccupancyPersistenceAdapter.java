package com.opencu.bookit.adapter.out.persistence.adapter;

import com.opencu.bookit.adapter.out.persistence.mapper.HallOccupancyMapper;
import com.opencu.bookit.adapter.out.persistence.repository.HallOccupancyRepository;
import com.opencu.bookit.application.port.out.statstics.LoadHallOccupancyPort;
import com.opencu.bookit.application.port.out.statstics.SaveHallOccupancyPort;
import com.opencu.bookit.domain.model.statistics.HallOccupancyModel;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class HallOccupancyPersistenceAdapter implements LoadHallOccupancyPort, SaveHallOccupancyPort {

    private final HallOccupancyRepository hallOccupancyRepository;
    private final HallOccupancyMapper hallOccupancyMapper;

    @Override
    public Optional<Integer> countReservedPlacesByDate(LocalDate date) {
        return hallOccupancyRepository.countReservedPlacesByDate(date);
    }

    @Override
    public List<HallOccupancyModel> findByDate(LocalDate date) {
        var entities = hallOccupancyRepository.findByDate(date);
        return hallOccupancyMapper.toModelList(entities);
    }

    @Override
    public Optional<HallOccupancyModel> findById(LocalDateTime currHour) {
        return hallOccupancyRepository.findById(currHour).map(hallOccupancyMapper::toModel);
    }

    @Override
    public HallOccupancyModel getByDateTime(LocalDateTime time) {
        return hallOccupancyRepository.findById(time)
                .map(hallOccupancyMapper::toModel)
                .orElseThrow(() -> new IllegalArgumentException("Hall occupancy not found for time: " + time));
    }

    @Override
    public HallOccupancyModel save(HallOccupancyModel hallOccupancyModel) {
        var entity = hallOccupancyMapper.toEntity(hallOccupancyModel);
        return hallOccupancyMapper.toModel(hallOccupancyRepository.save(entity));
    }
}

