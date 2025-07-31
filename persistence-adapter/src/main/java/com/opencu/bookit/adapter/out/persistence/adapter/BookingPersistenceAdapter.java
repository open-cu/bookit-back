package com.opencu.bookit.adapter.out.persistence.adapter;

import com.opencu.bookit.adapter.out.persistence.entity.AreaEntity;
import com.opencu.bookit.adapter.out.persistence.entity.BookingEntity;
import com.opencu.bookit.adapter.out.persistence.entity.UserEntity;
import com.opencu.bookit.adapter.out.persistence.mapper.BookingMapper;
import com.opencu.bookit.adapter.out.persistence.repository.AreaRepository;
import com.opencu.bookit.adapter.out.persistence.repository.BookingRepository;
import com.opencu.bookit.adapter.out.persistence.repository.UserRepository;
import com.opencu.bookit.application.port.out.booking.DeleteBookingPort;
import com.opencu.bookit.application.port.out.booking.LoadBookingPort;
import com.opencu.bookit.application.port.out.booking.SaveBookingPort;
import com.opencu.bookit.domain.model.booking.BookingModel;
import com.opencu.bookit.domain.model.booking.TimeTag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class BookingPersistenceAdapter implements
        LoadBookingPort, SaveBookingPort, DeleteBookingPort {
    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final AreaRepository areaRepository;
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
    public List<BookingModel> findAllIncludingTime(LocalDateTime date) {
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
    public Page<BookingModel> findWithFilters(
            LocalDate startDate,
            LocalDate endDate,
            Pageable pageable,
            UUID areaId,
            UUID userId
    ) {
        Specification<BookingEntity> spec = Specification.where(null);

        if (areaId != null && userId != null) {
            Optional<AreaEntity> areaOpt = areaRepository.findById(areaId);
            Optional<UserEntity> userOpt = userRepository.findById(userId);
            if (areaOpt.isPresent() && userOpt.isPresent()) {
                AreaEntity area = areaOpt.get();
                UserEntity user = userOpt.get();
                spec = spec.and((root, query, cb) ->
                    cb.and(
                            cb.equal(root.get("areaEntity"), area),
                            cb.equal(root.get("userEntity"), user)
                    )
                );
            }
        }
        if (startDate != null && endDate != null) {
            spec = spec.and((root, query, cb) ->
                cb.between(root.get("startTime"),
                        LocalDateTime.of(startDate, LocalTime.of(0,0,0)),
                        LocalDateTime.of(endDate, LocalTime.of(0,0,0)))
            );
        }
        return bookingRepository.findAll(spec, pageable).map(bookingMapper::toModel);
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

    @Override
    public void deleteById(UUID bookingId) {
        bookingRepository.deleteById(bookingId);
    }

    @Override
    public void deleteBookingAccordingToIndirectParameters(UUID userId, UUID areaId, LocalDateTime startTime, LocalDateTime endTime) {
        bookingRepository.deleteByUserIdAndAreaIdAndStartTimeAndEndTime(userId, areaId, startTime, endTime);
    }
}
