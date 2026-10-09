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
    public void saveStatisticBatch(List<StatisticModel> statistics) {
        if (statistics.isEmpty()) {
            return;
        }
        try {
            statisticDao.saveBatch(statistics);
            log.info("✅ Successfully saved batch of {} records to ClickHouse", statistics.size());
        } catch (Exception e) {
            log.warn("⚠️ Batch insert failed on database level. Switching to fallback one-by-one mode to isolate bad records. Reason: {}", e.getMessage());

            for (StatisticModel model: statistics) {
                try {
                    statisticDao.save(model);
                } catch (Exception singleException) {
                    log.error("❌ ClickHouse rejected single record (BookingID: {}, UserID: {}). Sending to DLQ.", model.bookingId(), model.userId(), singleException);
                    throw singleException;
                }
            }
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
