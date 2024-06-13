package com.aleksey.booking.hotels.api.response;


import java.util.List;

public record HotelPaginationResponse(Long hotelsCount, List<HotelResponse> hotels) {
}