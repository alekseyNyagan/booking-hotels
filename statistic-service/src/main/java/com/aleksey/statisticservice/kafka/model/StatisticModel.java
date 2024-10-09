package com.aleksey.statisticservice.kafka.model;

import java.time.LocalDate;
import java.util.UUID;

public record StatisticModel(UUID userId, LocalDate arrivalDate, LocalDate departureDate) {
}