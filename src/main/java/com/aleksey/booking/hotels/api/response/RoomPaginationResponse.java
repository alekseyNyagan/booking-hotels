package com.aleksey.booking.hotels.api.response;

import java.util.List;

public record RoomPaginationResponse(Long roomsCount, List<RoomResponse> rooms) {
}