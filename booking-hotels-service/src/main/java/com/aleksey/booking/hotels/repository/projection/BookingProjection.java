package com.aleksey.booking.hotels.repository.projection;

import java.time.LocalDate;
import java.util.UUID;

public record BookingProjection(Long id, LocalDate arrivalDate, LocalDate departureDate, UUID userId) {
}
