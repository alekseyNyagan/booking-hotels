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
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/booking")
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;

    @PostMapping
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_USER')")
    public ResponseEntity<BookingResponse> createBooking(@RequestBody @Valid UpsertBookingRequest upsertBookingRequest,
                                                         @AuthenticationPrincipal Jwt jwt) {
        return ResponseEntity.status(HttpStatus.CREATED).body(bookingService.createBooking(upsertBookingRequest, jwt));
    }

    @GetMapping("/bookingPage")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<BookingPaginationResponse> bookingPage(@RequestParam Integer pageSize, @RequestParam Integer pageNumber) {
        return ResponseEntity.ok(bookingService.getBookingPage(pageSize, pageNumber));
    }
}