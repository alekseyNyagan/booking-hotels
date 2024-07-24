package com.aleksey.booking.hotels.api.response;

import java.io.Serializable;

/**
 * DTO for {@link com.aleksey.booking.hotels.model.Hotel}
 */
public record HotelResponse(Long id, String name, String title, String city, String address,
                            Double distanceToCityCenter, Double rating, Integer marksCount) implements Serializable {
}