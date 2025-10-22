package com.aleksey.statisticservice.kafka.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public record StatisticModel(UUID eventId,
                             UUID userId,
                             Long bookingId,
                             Long hotelId,
                             String hotelCity,
                             List<Long> roomIds,
                             int roomsCount,
                             LocalDate arrivalDate,
                             LocalDate departureDate,
                             int totalNights,
                             BigDecimal totalCost,
                             String createdAt) {
}