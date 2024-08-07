package com.aleksey.booking.hotels.controller;

import com.aleksey.booking.hotels.api.request.UpsertBookingRequest;
import com.aleksey.booking.hotels.api.response.BookingPaginationResponse;
import com.aleksey.booking.hotels.api.response.BookingResponse;
import com.aleksey.booking.hotels.service.BookingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/booking")
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public ResponseEntity<BookingResponse> createBooking(@RequestBody @Valid UpsertBookingRequest upsertBookingRequest) {
        return ResponseEntity.status(HttpStatus.CREATED).body(bookingService.createBooking(upsertBookingRequest));
    }

    @GetMapping("/bookingPage")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<BookingPaginationResponse> bookingPage(@RequestParam Integer pageSize, @RequestParam Integer pageNumber) {
        return ResponseEntity.ok(bookingService.getBookingPage(pageSize, pageNumber));
    }
}