package com.aleksey.statisticservice.api.response;

import java.math.BigDecimal;
import java.util.UUID;

public record UserStatResponse(UUID userId, long bookingsCount, BigDecimal totalSpent) {
}
