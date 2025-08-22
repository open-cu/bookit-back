package com.opencu.bookit.adapter.in.web.mapper;

import com.opencu.bookit.adapter.in.web.dto.response.BookingResponse;
import com.opencu.bookit.domain.model.booking.BookingModel;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.util.List;
import java.util.Set;

@Mapper(
    componentModel = "spring",
    unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface BookingResponseMapper {
    
    @Mapping(target = "userId", expression = "java(bookingModel.getUserId())")
    @Mapping(target = "areaId", expression = "java(bookingModel.getAreaId())")
    @Mapping(target = "areaName", expression = "java(bookingModel.getAreaName())")
    BookingResponse toResponse(BookingModel bookingModel);
    
    List<BookingResponse> toResponseList(List<BookingModel> bookingModels);
    Set<BookingResponse> toResponseSet(Set<BookingModel> bookingModels);
}