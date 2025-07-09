package ru.tbank.bookit.book_it_backend.DTO;

import java.util.UUID;

public record EventOverlapResponse(
        UUID eventId1,
        String eventName1,
        UUID eventId2,
        String eventName2,
        long commonUsersCount,
        long event1TotalUsers,
        long event2TotalUsers,
        double overlapPercentage
) {}
