package com.aleksey.booking.hotels.api.response;

import com.aleksey.booking.hotels.annotation.MarkSize;
import jakarta.validation.constraints.NotNull;

public record RateRequest(Long hotelId, @MarkSize @NotNull Byte newMark) {
}