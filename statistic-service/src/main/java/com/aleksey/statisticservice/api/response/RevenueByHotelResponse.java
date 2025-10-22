package com.aleksey.statisticservice.api.response;

import java.math.BigDecimal;

public record RevenueByHotelResponse(Long hotelId, BigDecimal totalRevenue) {
}
