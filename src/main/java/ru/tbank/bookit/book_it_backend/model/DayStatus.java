package ru.tbank.bookit.book_it_backend.model;

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
    ELECTRICITY_OUTAGE("Отключение электроэнергии");

    private final String description;
}