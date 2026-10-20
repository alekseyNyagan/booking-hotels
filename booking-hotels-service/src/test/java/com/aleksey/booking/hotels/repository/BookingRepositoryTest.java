package com.aleksey.booking.hotels.repository;

import com.aleksey.booking.hotels.model.Booking;
import com.aleksey.booking.hotels.model.enums.BookingStatus;
import com.aleksey.booking.hotels.repository.projection.BookingProjection;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class BookingRepositoryTest extends BaseRepositoryTest {

    @Autowired
    private BookingRepository bookingRepository;

    @BeforeEach
    void setUp() {
        bookingRepository.deleteAll();
    }

    @Test
    void shouldFindOnlyStuckPendingBookingsOlderThanThreshold() {
        LocalDateTime now = LocalDateTime.now();

        Booking stuckBooking = new Booking();
        stuckBooking.setStatus(BookingStatus.PENDING_PAYMENT);
        stuckBooking.setCreatedAt(now.minusMinutes(15));
        bookingRepository.save(stuckBooking);

        Booking freshBooking = new Booking();
        freshBooking.setStatus(BookingStatus.PENDING_PAYMENT);
        freshBooking.setCreatedAt(now.minusMinutes(2));
        bookingRepository.save(freshBooking);

        Booking confirmedBooking = new Booking();
        confirmedBooking.setStatus(BookingStatus.CONFIRMED);
        confirmedBooking.setCreatedAt(now.minusMinutes(20));
        bookingRepository.save(confirmedBooking);

        LocalDateTime threshold = now.minusMinutes(10);
        List<Booking> result = bookingRepository.findAllByStatusAndCreatedAtBefore(BookingStatus.PENDING_PAYMENT, threshold);

        assertThat(result).hasSize(1);
        assertThat(result.getFirst().getId()).isEqualTo(stuckBooking.getId());
    }

    @Test
    void shouldCorrectlyLoadBookingDTOProjectionsWithoutFullEntityFootprint() {
        Booking booking = new Booking();
        booking.setArrivalDate(java.time.LocalDate.of(2026, 10, 20));
        booking.setDepartureDate(java.time.LocalDate.of(2026, 10, 25));
        bookingRepository.save(booking);

        Page<BookingProjection> projectionPage = bookingRepository.findBookings(PageRequest.of(0, 10));

        assertThat(projectionPage.getContent()).hasSize(1);
        BookingProjection targetProjection = projectionPage.getContent().getFirst();
        assertThat(targetProjection.id()).isEqualTo(booking.getId());
        assertThat(targetProjection.arrivalDate()).isEqualTo(booking.getArrivalDate());
    }
}
