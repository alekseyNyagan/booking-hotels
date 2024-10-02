package com.aleksey.booking.hotels.controller;

import com.aleksey.booking.hotels.api.request.UpsertBookingRequest;
import com.aleksey.booking.hotels.api.response.BookingPaginationResponse;
import com.aleksey.booking.hotels.api.response.BookingResponse;
import com.aleksey.booking.hotels.service.BookingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Booking API", description = "API for booking")
public class BookingController {

    private final BookingService bookingService;

    @PostMapping
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_USER')")
    @Operation(summary = "Create new booking", description = "Create new booking by provided data")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Booking created successfully", content = @Content(
                    schema = @Schema(implementation = BookingResponse.class)
            )),
            @ApiResponse(responseCode = "400", description = "Bad request")
    })
    public ResponseEntity<BookingResponse> createBooking(
            @Parameter(description = "Data for creating new booking", required = true) @RequestBody @Valid UpsertBookingRequest upsertBookingRequest,
            @Parameter(description = "JWT token", required = true, hidden = true) @AuthenticationPrincipal Jwt jwt) {
        return ResponseEntity.status(HttpStatus.CREATED).body(bookingService.createBooking(upsertBookingRequest, jwt));
    }

    @GetMapping("/bookingPage")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @Operation(summary = "Get page of bookings", description = "Return page of bookings by provided filter")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Bookings returned successfully", content = @Content(
                    schema = @Schema(implementation = BookingPaginationResponse.class)
            ))
    })
    public ResponseEntity<BookingPaginationResponse> bookingPage(
            @Parameter(description = "Page size", required = true, example = "10") @RequestParam Integer pageSize,
            @Parameter(description = "Page number", required = true, example = "1") @RequestParam Integer pageNumber) {
        return ResponseEntity.ok(bookingService.getBookingPage(pageSize, pageNumber));
    }
}