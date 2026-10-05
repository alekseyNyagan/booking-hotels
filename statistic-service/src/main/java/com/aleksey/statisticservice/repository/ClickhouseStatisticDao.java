package com.aleksey.statisticservice.repository;

import com.aleksey.statisticservice.api.response.*;
import com.aleksey.statisticservice.kafka.model.StatisticModel;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class ClickhouseStatisticDao implements StatisticDao {

    private final JdbcClient jdbcClient;

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
        String sql = """
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

        Long[] roomsArray = statisticModel.roomIds().toArray(new Long[0]);
        Timestamp createdAtTimestamp = Timestamp.from(Instant.parse(statisticModel.createdAt()));

        jdbcClient.sql(sql)
                .param("event_id", statisticModel.eventId())
                .param("booking_id", statisticModel.bookingId())
                .param("user_id", statisticModel.userId())
                .param("hotel_id", statisticModel.hotelId())
                .param("hotel_city", statisticModel.hotelCity())
                .param("room_ids", roomsArray)
                .param("rooms_count", statisticModel.roomsCount())
                .param("arrival_date", statisticModel.arrivalDate())
                .param("departure_date", statisticModel.departureDate())
                .param("total_nights", statisticModel.totalNights())
                .param("total_cost", statisticModel.totalCost())
                .param("created_at", createdAtTimestamp)
                .update();
    }
}
