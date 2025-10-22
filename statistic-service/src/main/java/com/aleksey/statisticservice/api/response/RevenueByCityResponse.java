package com.aleksey.statisticservice.api.response;

import java.math.BigDecimal;

public record RevenueByCityResponse(String city, BigDecimal totalRevenue) {
}
