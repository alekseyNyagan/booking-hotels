package com.aleksey.booking.hotels.api.response;

import java.io.Serializable;

public record RoomInfo(Long id, String name, String description, String number, Integer cost, Integer maxCountOfPeople,
                       Long hotelId) implements Serializable {
}