package com.aleksey.booking.hotels.api.request;

import java.io.Serializable;

public record RoomFilter(Integer pageSize, Integer pageNumber, Long roomId, String name,
                         Integer minCost, Integer maxCost, Integer countOfVisitors,
                         String arrivalDate, String departureDate, Long hotelId) implements Serializable {
}