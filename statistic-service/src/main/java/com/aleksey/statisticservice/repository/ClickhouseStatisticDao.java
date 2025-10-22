package com.aleksey.statisticservice.repository;

import com.aleksey.statisticservice.api.response.*;
import com.aleksey.statisticservice.kafka.model.StatisticModel;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class ClickhouseStatisticDao implements StatisticDao {

    private final JdbcClient jdbcClient;

    @Override
    public List<DailyBookingStatResponse> getDailyBookings(LocalDate from, LocalDate to) {
        String sql = """
            SELECT toDate(created_at) AS date, count() AS bookingsCount
            FROM booking_statistics
            WHERE created_at BETWEEN :from AND :to
            GROUP BY date
            ORDER BY date
        """;

        return jdbcClient.sql(sql)
                .param("from", from)
                .param("to", to)
                .query(DailyBookingStatResponse.class)
                .list();
    }

    @Override
    public List<UserStatResponse> getTopUsers(int limit) {
        String sql = """
            SELECT user_id, count() AS bookingsCount, sum(total_cost) AS totalSpent
            FROM booking_statistics
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
            SELECT hotel_city, sum(total_cost) AS totalRevenue
            FROM booking_statistics
            WHERE created_at BETWEEN :from AND :to
            GROUP BY hotel_city
            ORDER BY totalRevenue DESC
        """;

        return jdbcClient.sql(sql)
                .param("from", from)
                .param("to", to)
                .query(RevenueByCityResponse.class)
                .list();
    }

    @Override
    public List<RevenueByHotelResponse> getRevenueByHotel(LocalDate from, LocalDate to) {
        String sql = """
            SELECT hotel_id, sum(total_cost) AS totalRevenue
            FROM booking_statistics
            WHERE created_at BETWEEN :from AND :to
            GROUP BY hotel_id
            ORDER BY totalRevenue DESC
        """;

        return jdbcClient.sql(sql)
                .param("from", from)
                .param("to", to)
                .query(RevenueByHotelResponse.class)
                .list();
    }

    @Override
    public SummaryResponse getSummary(LocalDate from, LocalDate to) {
        String sql = """
            SELECT
                count() AS totalBookings,
                uniq(user_id) AS uniqueUsers,
                avg(total_nights) AS averageStayNights,
                avg(total_cost) AS averageBookingCost,
                sum(total_cost) AS totalRevenue
            FROM booking_statistics
            WHERE created_at BETWEEN :from AND :to
        """;

        return jdbcClient.sql(sql)
                .param("from", from)
                .param("to", to)
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

        jdbcClient.sql(sql)
                .param("event_id", statisticModel.eventId())
                .param("booking_id", statisticModel.bookingId())
                .param("user_id", statisticModel.userId())
                .param("hotel_id", statisticModel.hotelId())
                .param("hotel_city", statisticModel.hotelCity())
                .param("room_ids", statisticModel.roomIds().toArray())
                .param("rooms_count", statisticModel.roomsCount())
                .param("arrival_date", statisticModel.arrivalDate())
                .param("departure_date", statisticModel.departureDate())
                .param("total_nights", statisticModel.totalNights())
                .param("total_cost", statisticModel.totalCost())
                .param("created_at", statisticModel.createdAt())
                .update();
    }
}
