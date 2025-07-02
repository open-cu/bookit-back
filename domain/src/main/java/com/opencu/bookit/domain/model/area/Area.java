package com.opencu.bookit.domain.model.area;

import com.opencu.bookit.domain.model.booking.Booking;
import com.opencu.bookit.domain.model.ticket.Ticket;
import lombok.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
public class Area {
    private UUID id;
    private String name;
    private String description;
    private AreaType type;
    private AreaFeature features;
    private int capacity;
    private AreaStatus status;
    private List<Booking> bookings = new ArrayList<>();
    private List<Ticket> tickets = new ArrayList<>();
}
