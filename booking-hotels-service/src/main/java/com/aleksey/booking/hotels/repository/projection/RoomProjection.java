package com.aleksey.booking.hotels.repository.projection;

import java.io.Serializable;

public record RoomProjection(Long id, String name, String description, String number, Integer cost, Integer maxCountOfPeople,
                             Long hotelId, Long bookingId) implements Serializable {
}