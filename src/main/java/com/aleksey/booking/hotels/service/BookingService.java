package com.aleksey.booking.hotels.service;

import com.aleksey.booking.hotels.api.response.BookingListResponse;
import com.aleksey.booking.hotels.api.response.BookingResponse;
import com.aleksey.booking.hotels.api.request.UpsertBookingRequest;

public interface BookingService {

    BookingResponse createBooking(UpsertBookingRequest upsertBookingRequest);

    BookingListResponse allBookings();
}