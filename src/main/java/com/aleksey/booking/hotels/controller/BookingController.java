package com.aleksey.booking.hotels.controller;

import com.aleksey.booking.hotels.api.request.UpsertBookingRequest;
import com.aleksey.booking.hotels.api.response.BookingListResponse;
import com.aleksey.booking.hotels.api.response.BookingResponse;
import com.aleksey.booking.hotels.service.BookingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/booking")
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;

    @PostMapping
    public ResponseEntity<BookingResponse> createBooking(@RequestBody @Valid UpsertBookingRequest upsertBookingRequest) {
        return ResponseEntity.status(HttpStatus.CREATED).body(bookingService.createBooking(upsertBookingRequest));
    }

    @GetMapping
    public ResponseEntity<BookingListResponse> getBookings() {
        return ResponseEntity.ok(bookingService.allBookings());
    }
}