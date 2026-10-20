package com.aleksey.booking.hotels.repository;

import com.aleksey.booking.hotels.model.Booking;
import com.aleksey.booking.hotels.model.enums.BookingStatus;
import com.aleksey.booking.hotels.repository.projection.BookingProjection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {

    List<Booking> findAllByStatusAndCreatedAtBefore(BookingStatus bookingStatus, LocalDateTime thresholdTime);

    @Query(value = "SELECT new com.aleksey.booking.hotels.repository.projection.BookingProjection(b.id, b.arrivalDate, b.departureDate, b.userId) FROM Booking b")
    Page<BookingProjection> findBookings(Pageable pageable);
}