package com.aleksey.booking.hotels.api.request.response;

import java.util.UUID;

public record PaymentResponse(UUID transactionId, String status) {
}
