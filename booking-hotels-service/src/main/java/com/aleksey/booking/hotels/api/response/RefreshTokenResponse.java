package com.aleksey.booking.hotels.api.response;

import java.io.Serializable;

public record RefreshTokenResponse(String accessToken, String refreshToken) implements Serializable {
}