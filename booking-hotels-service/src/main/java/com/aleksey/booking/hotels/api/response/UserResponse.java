package com.aleksey.booking.hotels.api.response;

import java.io.Serializable;

/**
 * DTO for {@link com.aleksey.booking.hotels.model.User}
 */
public record UserResponse(Long id, String name, String password, String email) implements Serializable {
}