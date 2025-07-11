package com.opencu.bookit.domain.model.area;

import com.opencu.bookit.domain.model.booking.BookingModel;
import com.opencu.bookit.domain.model.ticket.TicketModel;
import lombok.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AreaModel {
    private UUID id;
    private String name;
    private String description;
    private AreaType type;
    private AreaFeature features;
    private int capacity;
    private AreaStatus status;
    private List<BookingModel> bookingModels = new ArrayList<>();
    private List<TicketModel> ticketModels = new ArrayList<>();
}
