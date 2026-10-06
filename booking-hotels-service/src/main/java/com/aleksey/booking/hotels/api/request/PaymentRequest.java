package com.aleksey.booking.hotels.api.request;

import java.math.BigDecimal;
import java.util.UUID;

public record PaymentRequest(Long bookingId, UUID userId, BigDecimal totalCost) {
}
