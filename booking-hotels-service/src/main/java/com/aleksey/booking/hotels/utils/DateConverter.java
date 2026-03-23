package com.aleksey.booking.hotels.utils;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class DateConverter {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    private DateConverter() {
        throw new IllegalStateException("Utility class");
    }

    public static LocalDate fromStringDateToLocalDate(String date) {
        return LocalDate.parse(date, DATE_FORMATTER);
    }
}
