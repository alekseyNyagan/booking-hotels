package com.aleksey.booking.hotels.service;

import com.aleksey.booking.hotels.api.response.BookingPaginationResponse;
import com.aleksey.booking.hotels.api.response.BookingResponse;
import com.aleksey.booking.hotels.api.request.UpsertBookingRequest;
import org.springframework.security.oauth2.jwt.Jwt;

public interface BookingService {

    BookingResponse createBooking(UpsertBookingRequest upsertBookingRequest, Jwt jwt);

    BookingPaginationResponse getBookingPage(Integer pageSize, Integer pageNumber);
}