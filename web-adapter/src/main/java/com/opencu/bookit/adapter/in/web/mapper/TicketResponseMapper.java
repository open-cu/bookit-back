package com.opencu.bookit.adapter.in.web.mapper;

import com.opencu.bookit.adapter.in.web.dto.response.TicketResponse;
import com.opencu.bookit.domain.model.ticket.TicketModel;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface TicketResponseMapper {

    @Mapping(target = "userId", source = "id.userId")
    @Mapping(target = "areaId", source = "id.areaId")
    TicketResponse toResponse(TicketModel ticketModel);

    List<TicketResponse> toResponseList(List<TicketModel> ticketModels);
}