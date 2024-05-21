package com.aleksey.booking.hotels.api.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

import java.io.Serializable;
import java.util.List;

/**
 * DTO for {@link com.aleksey.booking.hotels.model.Booking}
 */
public record UpsertBookingRequest(
        @JsonProperty("arrival_date") @NotBlank(message = "Дата прибытия должна быть указана!") String arrivalDate
        , @JsonProperty("departure_date") @NotBlank(message = "Дата отбытия должна быть указана!") String departureDate
        , @JsonProperty("room_ids") @NotEmpty(message = "Должна быть указана хотя бы одна комната!") List<Long> roomIds
        , @JsonProperty("user_id") Long userId) implements Serializable {
}