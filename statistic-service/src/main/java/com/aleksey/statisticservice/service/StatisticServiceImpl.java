package com.aleksey.statisticservice.service;

import com.aleksey.statisticservice.api.response.*;
import com.aleksey.statisticservice.kafka.model.StatisticModel;
import com.aleksey.statisticservice.repository.StatisticDao;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class StatisticServiceImpl implements StatisticService {

    private final StatisticDao statisticDao;

    @Override
    public void saveStatistic(StatisticModel statistic) {
        try {
            statisticDao.save(statistic);
            log.info("✅ Statistic saved to ClickHouse: bookingId={}, userId={}",
                    statistic.bookingId(), statistic.userId());
        } catch (Exception e) {
            log.error("❌ Failed to save statistic to ClickHouse", e);
        }
    }

    @Override
    public List<DailyBookingStatResponse> getDailyBookings(LocalDate from, LocalDate to) {
        return statisticDao.getDailyBookings(from, to);
    }

    @Override
    public List<UserStatResponse> getTopUsers(int limit) {
        return statisticDao.getTopUsers(limit);
    }

    @Override
    public List<RevenueByCityResponse> getRevenueByCity(LocalDate from, LocalDate to) {
        return statisticDao.getRevenueByCity(from, to);
    }

    @Override
    public List<RevenueByHotelResponse> getRevenueByHotel(LocalDate from, LocalDate to) {
        return statisticDao.getRevenueByHotel(from, to);
    }

    @Override
    public SummaryResponse getSummary(LocalDate from, LocalDate to) {
        return statisticDao.getSummary(from, to);
    }
}
