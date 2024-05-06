package com.aleksey.booking.hotels.api.request;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;

/**
 * DTO for {@link com.aleksey.booking.hotels.model.Hotel}
 */
public record UpsertHotelRequest(String name, String title, String city, String address,
                                 @JsonProperty(value = "distance_to_city_center") Float distanceToCityCenter) implements Serializable {
}