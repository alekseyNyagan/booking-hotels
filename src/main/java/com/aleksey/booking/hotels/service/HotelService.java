package com.aleksey.booking.hotels.service;

import com.aleksey.booking.hotels.api.request.HotelFilter;
import com.aleksey.booking.hotels.api.request.UpsertHotelRequest;
import com.aleksey.booking.hotels.api.response.HotelListResponse;
import com.aleksey.booking.hotels.api.response.HotelPaginationResponse;
import com.aleksey.booking.hotels.api.response.HotelResponse;
import com.aleksey.booking.hotels.api.response.RateRequest;

public interface HotelService {
    HotelResponse findById(Long id);

    HotelResponse createHotel(UpsertHotelRequest upsertHotelRequest);

    HotelResponse updateHotel(Long id, UpsertHotelRequest upsertHotelRequest);

    void deleteHotel(Long id);

    HotelListResponse findAllHotels();

    void rateHotel(RateRequest rateRequest);

    HotelPaginationResponse filterBy(HotelFilter hotelFilter);
}
