package com.opencu.bookit.domain.model.schedule;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum DayStatus {
    WEEKEND("Выходной день"),
    HOLIDAY("Государственный праздник"),

    TECHNICAL_WORK("Технические работы"),
    SANITARY_DAY("Санитарный день"),
    PRIVATE_EVENT("Частное мероприятие"),

    WEATHER_EMERGENCY("Чрезвычайная погодная ситуация"),
    QUARANTINE("Карантинные мероприятия"),
    ELECTRICITY_OUTAGE("Отключение электроэнергии"),

    UNDEFINED_REASON("Неопределенная ситуация");

    private final String description;
}