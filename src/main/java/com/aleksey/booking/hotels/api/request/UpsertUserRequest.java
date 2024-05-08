package com.aleksey.booking.hotels.api.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

import java.io.Serializable;

/**
 * DTO for {@link com.aleksey.booking.hotels.model.User}
 */
public record UpsertUserRequest(@NotBlank(message = "Имя пользователя должно быть заполнено!") String name
        , @NotBlank(message = "Пароль должен быть заполнен!") String password
        , @Email(regexp = "^(.+)@(\\S+)$", message = "Email должен быть в формате username@domain.com")
                                @NotBlank(message = "Email должен быть заполнен!") String email) implements Serializable {
}