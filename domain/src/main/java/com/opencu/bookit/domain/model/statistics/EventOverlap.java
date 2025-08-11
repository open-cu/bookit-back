package com.opencu.bookit.domain.model.statistics;

import java.math.BigDecimal;
import java.util.UUID;

public record EventOverlap(
        UUID eventId1,
        String eventName1,
        UUID eventId2,
        String eventName2,
        Long commonUsersCount,
        Long event1TotalUsers,
        Long event2TotalUsers,
        BigDecimal overlapPercentage
) {}
