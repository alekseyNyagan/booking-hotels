package com.aleksey.statisticservice.api.response;

import java.time.LocalDate;

public record DailyBookingStatResponse(LocalDate date, long bookingsCount) {
}
