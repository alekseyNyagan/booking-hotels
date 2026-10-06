package com.aleksey.booking.hotels.api.response;

import java.util.UUID;

public record PaymentResponse(UUID transactionId, String status) {
}
