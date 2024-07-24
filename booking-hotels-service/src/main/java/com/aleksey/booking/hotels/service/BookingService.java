package com.aleksey.booking.hotels.service;

import com.aleksey.booking.hotels.api.response.BookingPaginationResponse;
import com.aleksey.booking.hotels.api.response.BookingResponse;
import com.aleksey.booking.hotels.api.request.UpsertBookingRequest;

public interface BookingService {

    BookingResponse createBooking(UpsertBookingRequest upsertBookingRequest);

    BookingPaginationResponse getBookingPage(Integer pageSize, Integer pageNumber);
}