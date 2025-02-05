package com.aleksey.booking.hotels.repository;

import com.aleksey.booking.hotels.model.Booking;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {

    @Override
    @NonNull
    Page<Booking> findAll(@NonNull Pageable pageable);
}