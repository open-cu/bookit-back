package ru.tbank.bookit.book_it_backend.model;

import lombok.Getter;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Getter
public enum StatsPeriod {
    WEEK("week", 1),
    FORTNIGHT("fortnight", 2),
    MONTH("month", 4),
    QUARTER("quarter", 12);

    private final String periodName;
    private final int weeksCount;

    private static final Map<String, StatsPeriod> LOOKUP = Arrays.stream(values())
            .collect(Collectors.toMap(StatsPeriod::getPeriodName, Function.identity()));

    StatsPeriod(String periodName, int weeksCount) {
        this.periodName = periodName;
        this.weeksCount = weeksCount;
    }

    public static StatsPeriod fromString(String periodName) {
        StatsPeriod period = LOOKUP.get(periodName.toLowerCase());
        if (period == null) {
            throw new IllegalArgumentException("Unknown period: " + periodName);
        }
        return period;
    }
}