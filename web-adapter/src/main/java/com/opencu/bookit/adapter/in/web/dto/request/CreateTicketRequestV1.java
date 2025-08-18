package com.opencu.bookit.adapter.in.web.dto.request;

import com.opencu.bookit.domain.model.ticket.TicketPriority;
import com.opencu.bookit.domain.model.ticket.TicketStatus;
import com.opencu.bookit.domain.model.ticket.TicketType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record CreateTicketRequestV1(
        @NotNull
        UUID userId,

        @NotNull
        UUID areaId,

        @NotNull
        TicketType type,

        String description,

        @Schema(
                description = """
                Represents current priority of the ticket.
                Possible values:
                        DEFAULT - ticket is created by user and hasn't been reviewed by admin yet,
                        LOW,
                        MEDIUM,
                        HIGH,
                        CRITICAL.
                """
        )
        @Nullable
        TicketPriority priority,
        @Schema(
                description = """
                        Represents current status of the ticket
                        Possible values:
                                OPEN - ticket is created, but not has been handled yet,
                                IN_PROGRESS - a responsible is handling the ticket,
                                ON_HOLD - ticket handling has been suspended. Providing a reason is required,
                                RESOLVED - ticket's problem is solved, but confirmation from the sender of the ticket is required,
                                CLOSED - terminal state. Problem is solved, confirmation is received,
                                REJECTED - ticket is rejected. Providing a reason is required.
                        """
        )
        @Nullable
        TicketStatus status
) {}
