package ru.tbank.bookit.book_it_backend.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.tbank.bookit.book_it_backend.DTO.AreaResponse;
import ru.tbank.bookit.book_it_backend.DTO.CreateBookingRequest;
import ru.tbank.bookit.book_it_backend.model.Area;
import ru.tbank.bookit.book_it_backend.model.AreaFeature;

import java.util.Set;
import java.util.stream.Collectors;

/**
 * Интерфейс для маппинга между сущностью Area и DTO
 */
@Mapper(componentModel = "spring")
public interface AreaMapper {

    /**
     * Преобразует сущность Area в AreaResponse
     */
    AreaResponse toAreaResponse(Area area);

    /**
     * Преобразует сущность Area в CreateBookingRequest
     */
    @Mapping(target = "areaId", source = "id")
    @Mapping(target = "userId", ignore = true)
    @Mapping(target = "timePeriods", ignore = true)
    @Mapping(target = "quantity", constant = "1")
    CreateBookingRequest toDTO(Area area);

    /**
     * Преобразует CreateBookingRequest в сущность Area
     */
    @Mapping(target = "id", source = "areaId")
    @Mapping(target = "name", ignore = true)
    @Mapping(target = "description", ignore = true)
    // Добавьте здесь игнорирование других полей Area, которых нет в CreateBookingRequest
    Area toEntity(CreateBookingRequest dto);

    /**
     * Метод для преобразования Set<AreaFeature> в Set<String>
     */
    default Set<String> map(Set<AreaFeature> features) {
        if (features == null) {
            return null;
        }
        return features.stream()
                .map(Enum::name)
                .collect(Collectors.toSet());
    }
}
