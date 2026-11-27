package com.aleksey.statisticservice.repository;

import com.aleksey.statisticservice.api.response.*;
import com.aleksey.statisticservice.kafka.model.StatisticModel;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
@RequiredArgsConstructor
public class ClickhouseStatisticDao implements StatisticDao {

    private static final String INSERT_SQL = """
                INSERT INTO booking_statistics (
                    event_id,
                    booking_id,
                    user_id,
                    hotel_id,
                    hotel_city,
                    room_ids,
                    rooms_count,
                    arrival_date,
                    departure_date,
                    total_nights,
                    total_cost,
                    created_at
                ) VALUES (
                    :event_id,
                    :booking_id,
                    :user_id,
                    :hotel_id,
                    :hotel_city,
                    :room_ids,
                    :rooms_count,
                    :arrival_date,
                    :departure_date,
                    :total_nights,
                    :total_cost,
                    :created_at
                )
            """;
    private final JdbcClient jdbcClient;

    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private static final String FROM_DATE_TIME_PARAMETER = "fromDateTime";
    private static final String TO_DATE_TIME_PARAMETER = "toDateTime";

    @Override
    public List<DailyBookingStatResponse> getDailyBookings(LocalDate from, LocalDate to) {
        String sql = """
            SELECT toDate(created_at) AS date, count(DISTINCT booking_id) AS bookingsCount
            FROM booking_statistics
            WHERE created_at BETWEEN :fromDateTime AND :toDateTime
            GROUP BY date
            ORDER BY date
        """;

        return jdbcClient.sql(sql)
                .param(FROM_DATE_TIME_PARAMETER, from.atStartOfDay())
                .param(TO_DATE_TIME_PARAMETER, to.atTime(LocalTime.MAX))
                .query(DailyBookingStatResponse.class)
                .list();
    }

    @Override
    public List<UserStatResponse> getTopUsers(int limit) {
        String sql = """
            SELECT user_id, count(DISTINCT booking_id) AS bookingsCount, sum(latest_cost) AS totalSpent
            FROM (
                SELECT user_id, booking_id, argMax(total_cost, created_at) AS latest_cost
                FROM booking_statistic
                WHERE created_at BETWEEN :fromDateTime AND :toDateTime
                GROUP_BY user_id, booking_id
            )
            GROUP BY user_id
            ORDER BY bookingsCount DESC
            LIMIT :limit
        """;

        return jdbcClient.sql(sql)
                .param("limit", limit)
                .query(UserStatResponse.class)
                .list();
    }

    @Override
    public List<RevenueByCityResponse> getRevenueByCity(LocalDate from, LocalDate to) {
        String sql = """
            SELECT hotel_city, sum(latest_cost) AS totalRevenue
            FROM (
                SELECT hotel_city, booking_id, argMax(total_cost, created_at) AS latest_cost
                FROM booking_statistic
                WHERE created_at BETWEEN :fromDateTime AND :toDateTime
                GROUP_BY hotel_city, booking_id
            )
            GROUP BY hotel_city
            ORDER BY totalRevenue DESC
        """;

        return jdbcClient.sql(sql)
                .param(FROM_DATE_TIME_PARAMETER, from.atStartOfDay())
                .param(TO_DATE_TIME_PARAMETER, to.atTime(LocalTime.MAX))
                .query(RevenueByCityResponse.class)
                .list();
    }

    @Override
    public List<RevenueByHotelResponse> getRevenueByHotel(LocalDate from, LocalDate to) {
        String sql = """
            SELECT hotel_id, sum(latest_cost) AS totalRevenue
            FROM (
                SELECT hotel_id, booking_id, argMax(total_cost, created_at) AS latest_cost
                FROM booking-statistic
                WHERE created_at BETWEEN :fromDateTime AND :toDateTime
                GROUP_BY hotel_id, booking_id
            )
            GROUP BY hotel_id
            ORDER BY totalRevenue DESC
        """;

        return jdbcClient.sql(sql)
                .param(FROM_DATE_TIME_PARAMETER, from.atStartOfDay())
                .param(TO_DATE_TIME_PARAMETER, to.atTime(LocalTime.MAX))
                .query(RevenueByHotelResponse.class)
                .list();
    }

    @Override
    public SummaryResponse getSummary(LocalDate from, LocalDate to) {
        String sql = """
            SELECT
                count(DISTINCT booking_id) AS totalBookings,
                uniq(user_id) AS uniqueUsers,
                avg(latest_nights) AS averageStayNights,
                avg(latest_cost) AS averageBookingCost,
                sum(latest_cost) AS totalRevenue
            FROM (
                SELECT
                    booking_id,
                    user_id,
                    argMax(total_nights, created_at) AS latest_nights,
                    argMax(total_cost, created_at) AS latest_cost
                FROM booking_statistic
                WHERE created_at BETWEEN :fromDateTime AND :toDateTime
                GROUP_BY booking_id, user_id
            )
        """;

        return jdbcClient.sql(sql)
                .param(FROM_DATE_TIME_PARAMETER, from.atStartOfDay())
                .param(TO_DATE_TIME_PARAMETER, to.atTime(LocalTime.MAX))
                .query(SummaryResponse.class)
                .single();
    }

    @Override
    public void save(StatisticModel statisticModel) {
        jdbcClient.sql(INSERT_SQL)
                .params(toParams(statisticModel))
                .update();
    }

    @Override
    public void saveBatch(List<StatisticModel> statistics) {
        if (statistics.isEmpty()) {
            return;
        }

        var batchSpec = jdbcClient.sql(INSERT_SQL).batch();

        statistics.forEach(model -> batchSpec.entry(batchEntry -> toParams(model).forEach(batchEntry::param)));

        batchSpec.update();
    }

    private Map<String, Object> toParams(StatisticModel model) {
        Map<String, Object> params = new HashMap<>();
        params.put("event_id", model.eventId());
        params.put("booking_id", model.bookingId());
        params.put("user_id", model.userId());
        params.put("hotel_id", model.hotelId());
        params.put("hotel_city", model.hotelCity());
        params.put("room_ids", model.roomIds().toArray(new Long[0]));
        params.put("rooms_count", model.roomsCount());
        params.put("arrival_date", model.arrivalDate());
        params.put("departure_date", model.departureDate());
        params.put("total_nights", model.totalNights());
        params.put("total_cost", model.totalCost());
        params.put("created_at", formatDate(model));
        return params;
    }

    private Timestamp formatDate(StatisticModel statisticModel) {
        LocalDateTime localDateTime = LocalDateTime.parse(statisticModel.createdAt(), formatter);
        return Timestamp.valueOf(localDateTime);
    }
}
