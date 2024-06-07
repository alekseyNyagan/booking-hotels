package com.aleksey.booking.hotels.api.response;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;

/**
 * DTO for {@link com.aleksey.booking.hotels.model.Booking}
 */
public record BookingResponse(Long id, LocalDate arrivalDate, LocalDate departureDate, List<RoomInfo> rooms,
                              UserResponse user) implements Serializable {
}