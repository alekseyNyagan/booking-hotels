package com.aleksey.booking.hotels.api.request;

import java.io.Serializable;

public record HotelFilter(Integer pageSize, Integer pageNumber, Long hotelId , String hotelName,
                          String title, String city, String address, Double distance,
                          Double rating, Integer marksCount) implements Serializable {
}