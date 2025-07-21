package com.opencu.bookit.adapter.out.persistence.adapter;

import com.opencu.bookit.adapter.out.persistence.entity.BookingEntity;
import com.opencu.bookit.adapter.out.persistence.mapper.BookingMapper;
import com.opencu.bookit.adapter.out.persistence.repository.BookingRepository;
import com.opencu.bookit.application.port.out.booking.LoadBookingPort;
import com.opencu.bookit.application.port.out.booking.SaveBookingPort;
import com.opencu.bookit.domain.model.booking.BookingModel;
import com.opencu.bookit.domain.model.booking.TimeTag;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class BookingPersistenceAdapter implements LoadBookingPort, SaveBookingPort {
    private final BookingRepository bookingRepository;
    private final BookingMapper bookingMapper;
    @Value("${booking.zone-id}")
    private ZoneId zoneId;

    @Override
    public Optional<BookingModel> findById(UUID bookingId) {
        return bookingRepository.findById(bookingId).map(bookingMapper::toModel);
    }

    @Override
    public List<BookingModel> loadBookingsByUser(UUID userId, TimeTag timeTag) {
        LocalDateTime now = LocalDateTime.now(zoneId);
        List<BookingEntity> bookingEntities = switch (timeTag) {
            case CURRENT -> bookingRepository.findCurrentBookingsByUser(userId, now);
            case FUTURE -> bookingRepository.findFutureBookingsByUser(userId, now);
            case PAST -> bookingRepository.findPastBookingsByUser(userId, now);
        };
        return bookingMapper.toModelList(bookingEntities);
    }

    @Override
    public List<BookingModel> findByAreaId(UUID areaId) {
        return bookingMapper.toModelList(bookingRepository.findByAreaId(areaId));
    }

    @Override
    public List<BookingModel> findByStartDatetime(LocalDateTime date) {
        return bookingMapper.toModelList(bookingRepository.findByStartDatetime(date));
    }

    @Override
    public List<BookingModel> findByDatetime(LocalDateTime date) {
        return bookingMapper.toModelList(bookingRepository.findByDatetime(date));
    }

    @Override
    public List<BookingModel> findByDateAndArea(LocalDate date, UUID areaId) {
        return bookingMapper.toModelList(bookingRepository.findByDateAndArea(date, areaId));
    }

    @Override
    public List<BookingModel> findAll() {
        return bookingMapper.toModelList(bookingRepository.findAll());
    }

    @Override
    public BookingModel save(BookingModel bookingModel) {
        BookingEntity entity = bookingMapper.toEntity(bookingModel);
        BookingEntity savedEntity = bookingRepository.save(entity);
        return bookingMapper.toModel(savedEntity);
    }

    @Override
    public List<BookingModel> saveAll(Set<BookingModel> bookingModels) {
        List<BookingEntity> entities = bookingMapper.toEntityList(bookingModels);
        return bookingMapper.toModelList(bookingRepository.saveAll(entities));
    }
}
