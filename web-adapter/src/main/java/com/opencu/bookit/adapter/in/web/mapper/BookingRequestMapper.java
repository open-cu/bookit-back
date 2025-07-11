package com.opencu.bookit.adapter.in.web.mapper;

import com.opencu.bookit.adapter.in.web.dto.request.CreateBookingRequest;
import com.opencu.bookit.adapter.in.web.dto.request.UpdateBookingRequest;
import com.opencu.bookit.application.port.in.booking.CRUDBookingUseCase;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface BookingRequestMapper {
    CRUDBookingUseCase.CreateBookingCommand toCommand(CreateBookingRequest request);

    CRUDBookingUseCase.UpdateBookingQuery toQuery(UpdateBookingRequest request);
}