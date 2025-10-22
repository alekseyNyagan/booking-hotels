package com.aleksey.statisticservice.controller;

import com.aleksey.statisticservice.api.response.*;
import com.aleksey.statisticservice.service.StatisticService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("api/v1/statistics")
@RequiredArgsConstructor
public class StatisticController {

    private final StatisticService statisticService;

    @GetMapping("/bookings/daily")
    public List<DailyBookingStatResponse> getDailyBookings(
            @RequestParam LocalDate from,
            @RequestParam LocalDate to) {
        return statisticService.getDailyBookings(from, to);
    }

    @GetMapping("/users/top")
    public List<UserStatResponse> getTopUsers(
            @RequestParam(defaultValue = "10") int limit) {
        return statisticService.getTopUsers(limit);
    }

    @GetMapping("/revenue/by-city")
    public List<RevenueByCityResponse> getRevenueByCity(
            @RequestParam LocalDate from,
            @RequestParam LocalDate to) {
        return statisticService.getRevenueByCity(from, to);
    }

    @GetMapping("/revenue/by-hotel")
    public List<RevenueByHotelResponse> getRevenueByHotel(
            @RequestParam LocalDate from,
            @RequestParam LocalDate to) {
        return statisticService.getRevenueByHotel(from, to);
    }

    @GetMapping("/summary")
    public SummaryResponse getSummary(
            @RequestParam LocalDate from,
            @RequestParam LocalDate to) {
        return statisticService.getSummary(from, to);
    }
}