package com.aleksey.statisticservice.repository;

import com.aleksey.statisticservice.api.response.*;
import com.aleksey.statisticservice.kafka.model.StatisticModel;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class ClickhouseStatisticDao implements StatisticDao {

    private final NamedParameterJdbcTemplate jdbcTemplate;

    @Override
    public List<DailyBookingStatResponse> getDailyBookings(LocalDate from, LocalDate to) {
        String sql = """
            SELECT toDate(created_at) AS date, count() AS bookingsCount
            FROM booking_statistics
            WHERE created_at BETWEEN :from AND :to
            GROUP BY date
            ORDER BY date
        """;

        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("from", from)
                .addValue("to", to);

        return jdbcTemplate.query(sql, params, (rs, i) ->
                new DailyBookingStatResponse(
                        rs.getDate("date").toLocalDate(),
                        rs.getLong("bookingsCount")
                )
        );
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

        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("limit", limit);

        return jdbcTemplate.query(sql, params, (rs, i) ->
                new UserStatResponse(
                        UUID.fromString(rs.getString("user_id")),
                        rs.getLong("bookingsCount"),
                        rs.getBigDecimal("totalSpent")
                )
        );
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

        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("from", from)
                .addValue("to", to);

        return jdbcTemplate.query(sql, params, (rs, i) ->
                new RevenueByCityResponse(
                        rs.getString("hotel_city"),
                        rs.getBigDecimal("totalRevenue")
                )
        );
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

        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("from", from)
                .addValue("to", to);

        return jdbcTemplate.query(sql, params, (rs, i) ->
                new RevenueByHotelResponse(
                        rs.getLong("hotel_id"),
                        rs.getBigDecimal("totalRevenue")
                )
        );
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

        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("from", from)
                .addValue("to", to);

        return jdbcTemplate.queryForObject(sql, params, (rs, i) ->
                new SummaryResponse(
                        rs.getLong("totalBookings"),
                        rs.getLong("uniqueUsers"),
                        rs.getDouble("averageStayNights"),
                        rs.getBigDecimal("averageBookingCost"),
                        rs.getBigDecimal("totalRevenue")
                )
        );
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

        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("event_id", statisticModel.eventId())
                .addValue("booking_id", statisticModel.bookingId())
                .addValue("user_id", statisticModel.userId())
                .addValue("hotel_id", statisticModel.hotelId())
                .addValue("hotel_city", statisticModel.hotelCity())
                .addValue("room_ids", statisticModel.roomIds().toArray())
                .addValue("rooms_count", statisticModel.roomsCount())
                .addValue("arrival_date", statisticModel.arrivalDate())
                .addValue("departure_date", statisticModel.departureDate())
                .addValue("total_nights", statisticModel.totalNights())
                .addValue("total_cost", statisticModel.totalCost())
                .addValue("created_at", statisticModel.createdAt());

        jdbcTemplate.update(sql, params);
    }
}
