package com.aleksey.statisticservice.service;

import com.aleksey.statisticservice.api.response.*;
import com.aleksey.statisticservice.kafka.model.StatisticModel;

import java.time.LocalDate;
import java.util.List;

public interface StatisticService {
    void saveStatisticBatch(List<StatisticModel> statistics);

    List<DailyBookingStatResponse> getDailyBookings(LocalDate from, LocalDate to);

    List<UserStatResponse> getTopUsers(int limit);

    List<RevenueByCityResponse> getRevenueByCity(LocalDate from, LocalDate to);

    List<RevenueByHotelResponse> getRevenueByHotel(LocalDate from, LocalDate to);

    SummaryResponse getSummary(LocalDate from, LocalDate to);
}
