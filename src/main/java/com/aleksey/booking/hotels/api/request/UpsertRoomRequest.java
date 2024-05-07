package com.aleksey.booking.hotels.api.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;
import org.hibernate.validator.constraints.Range;

import java.io.Serializable;

/**
 * DTO for {@link com.aleksey.booking.hotels.model.Room}
 */
public record UpsertRoomRequest(@NotBlank(message = "Название комнаты должно быть заполнено!") String name
        , @NotBlank(message = "Описание комнаты должно быть заполнено!") String description
        , @NotBlank(message = "Номер комнаты должен быть заполнен!") String number
        , @PositiveOrZero(message = "Стоимость номера должны выше нуля!") Integer cost
        , @JsonProperty(value = "max_count_of_people")
                                @Range(min = 1, max = 4, message = "Вместимость комнаты должна быть от 1 до 4 человек!") Integer maxCountOfPeople)
        implements Serializable {
}