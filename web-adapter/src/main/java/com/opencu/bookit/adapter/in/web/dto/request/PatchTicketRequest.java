package com.opencu.bookit.adapter.in.web.dto.request;

import com.opencu.bookit.domain.model.ticket.TicketType;

public record PatchTicketRequest(
        TicketType type,
        String description
){}
