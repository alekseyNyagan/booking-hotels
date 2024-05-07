package com.aleksey.booking.hotels.api.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;

import java.io.Serializable;

/**
 * DTO for {@link com.aleksey.booking.hotels.model.Hotel}
 */
public record UpsertHotelRequest(
        @NotBlank(message = "Название отеля должно быть заполнено!") String name
        , @NotBlank(message = "Заголовок объявления должен быть заполнен!") String title
        , @NotBlank(message = "Город должен быть заполнен!") String city
        , @NotBlank(message = "Адрес должен быть заполнен!") String address
        , @JsonProperty(value = "distance_to_city_center")
        @PositiveOrZero(message = "Дистанция до центра города не может быть меньше нуля!") Float distanceToCityCenter) implements Serializable {
}