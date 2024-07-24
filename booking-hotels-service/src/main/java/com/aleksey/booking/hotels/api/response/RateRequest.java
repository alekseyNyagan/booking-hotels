package com.aleksey.booking.hotels.api.response;

import jakarta.validation.constraints.Size;

public record RateRequest(Long hotelId, @Size(min = 1, max = 5, message = "Оценка может быть от 1 до 5!") Byte newMark) {
}