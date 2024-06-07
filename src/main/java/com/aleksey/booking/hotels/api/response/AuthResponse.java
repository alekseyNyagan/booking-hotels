package com.aleksey.booking.hotels.api.response;

import lombok.Builder;

import java.io.Serializable;
import java.util.List;

@Builder
public record AuthResponse(Long id, String token, String refreshToken, String username, String email, List<String> roles) implements Serializable {
}