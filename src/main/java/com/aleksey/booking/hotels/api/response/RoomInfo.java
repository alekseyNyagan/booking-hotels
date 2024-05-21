package com.aleksey.booking.hotels.api.response;

public record RoomInfo(Long id, String name, String description, String number, Integer cost, Integer maxCountOfPeople) {
}