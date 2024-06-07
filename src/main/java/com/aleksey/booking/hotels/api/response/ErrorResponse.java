package com.aleksey.booking.hotels.api.response;

import lombok.Builder;

@Builder
public record ErrorResponse(String errorMessage) {
}