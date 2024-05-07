package com.aleksey.booking.hotels.api.response;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;

/**
 * DTO for {@link com.aleksey.booking.hotels.model.Room}
 */
public record RoomResponse(Long id, String name, String description, String number, Integer cost,
                           Integer maxCountOfPeople, List<LocalDate> unavailableDates) implements Serializable {
}