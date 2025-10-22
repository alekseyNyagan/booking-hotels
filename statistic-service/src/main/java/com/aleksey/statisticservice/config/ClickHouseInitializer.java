package com.aleksey.statisticservice.config;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ClickHouseInitializer implements CommandLineRunner {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public void run(String... args) {
        jdbcTemplate.execute("""
            CREATE TABLE IF NOT EXISTS booking_statistics (
                event_id UUID,
                booking_id UInt64,
                user_id UUID,
                hotel_id UInt64,
                hotel_city String,
                room_ids Array(UInt64),
                rooms_count UInt8,
                arrival_date Date,
                departure_date Date,
                total_nights UInt16,
                total_cost Decimal(10,2),
                created_at DateTime
            )
            ENGINE = MergeTree()
            PARTITION BY toYYYYMM(created_at)
            ORDER BY (hotel_id, created_at)
        """);
    }
}
