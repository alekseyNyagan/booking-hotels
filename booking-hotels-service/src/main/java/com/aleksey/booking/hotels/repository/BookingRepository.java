package com.aleksey.booking.hotels.repository;

import com.aleksey.booking.hotels.model.Booking;
import com.aleksey.booking.hotels.model.enums.BookingStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {

    @Override
    Page<Booking> findAll(Pageable pageable);

    List<Booking> findAllByStatusAndCreatedAtBefore(BookingStatus bookingStatus, LocalDateTime thresholdTime);
}