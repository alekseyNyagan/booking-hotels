package com.aleksey.statisticservice.api.response;

import java.math.BigDecimal;

public record SummaryResponse(
        long totalBookings,
        long uniqueUsers,
        double averageStayNights,
        BigDecimal averageBookingCost,
        BigDecimal totalRevenue
) {
}
