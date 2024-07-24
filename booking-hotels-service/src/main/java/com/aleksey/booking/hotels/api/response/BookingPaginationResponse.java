package com.aleksey.booking.hotels.api.response;

import java.util.List;

public record BookingPaginationResponse(Long bookingsCount, List<BookingResponse> bookings) {
}