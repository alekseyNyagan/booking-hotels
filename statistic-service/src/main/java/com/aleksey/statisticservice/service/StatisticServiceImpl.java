package com.aleksey.statisticservice.service;

import com.aleksey.statisticservice.api.response.*;
import com.aleksey.statisticservice.kafka.model.StatisticModel;
import com.aleksey.statisticservice.repository.StatisticDao;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.BadSqlGrammarException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.CannotCreateTransactionException;

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
        } catch (DataAccessResourceFailureException | CannotCreateTransactionException e) {
            log.error("🔌 Infrastructure failure detected during ClickHouse batch insert. Triggering Kafka Retry Topics pipeline.", e);
            throw e;
        } catch (DataIntegrityViolationException | BadSqlGrammarException _) {
            processRowByRowFallBack(statistics);
        } catch (Exception e) {
            log.error("❓ Unexpected critical error during batch processing", e);
            throw e;
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

    private void processRowByRowFallBack(List<StatisticModel> statistics) {
        for (StatisticModel model: statistics) {
            try {
                statisticDao.save(model);
            } catch (DataIntegrityViolationException | BadSqlGrammarException e) {
                log.error("☣️ Fatal business data issue for Event ID: {}. Routing explicitly to DLQ.", model.eventId(), e);
                throw e;
            } catch (Exception e) {
                log.error("🔄 Temporary fallback error for Event ID: {}", model.eventId(), e);
            }
        }
    }
}
