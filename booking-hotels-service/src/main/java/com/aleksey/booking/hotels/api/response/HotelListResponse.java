package com.aleksey.booking.hotels.api.response;

import java.util.List;

public record HotelListResponse(List<HotelResponse> hotels) {
}
