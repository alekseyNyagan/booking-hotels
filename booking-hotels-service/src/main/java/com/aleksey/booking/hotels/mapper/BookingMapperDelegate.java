package com.aleksey.booking.hotels.mapper;

import com.aleksey.booking.hotels.api.response.BookingResponse;
import com.aleksey.booking.hotels.model.Booking;
import com.aleksey.booking.hotels.model.Room;
import com.aleksey.booking.hotels.model.UnavailableDate;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

public abstract class BookingMapperDelegate implements BookingMapper {

    @Autowired
    private RoomMapper roomMapper;

    @Override
    public Booking toEntity(UUID userId, List<Room> rooms, LocalDate arrivalDate, LocalDate departureDate) {
        Set<UnavailableDate> unavailableDates = arrivalDate.datesUntil(departureDate).map(localDate -> {
            UnavailableDate unavailableDate = new UnavailableDate();
            unavailableDate.setDate(localDate);
            return unavailableDate;
        }).collect(Collectors.toSet());
        rooms.forEach(room -> room.addUnavailableDates(unavailableDates));
        Booking booking = new Booking();
        booking.setArrivalDate(arrivalDate);
        booking.setDepartureDate(departureDate);
        booking.setRooms(rooms);
        booking.setUserId(userId);
        return booking;
    }

    @Override
    public BookingResponse toDto(Booking booking) {
        return new BookingResponse(
                booking.getId()
                , booking.getArrivalDate()
                , booking.getDepartureDate()
                , roomMapper.roomListToRoomInfoList(booking.getRooms())
                , booking.getUserId());
    }

    @Override
    public List<BookingResponse> bookingListToResponseList(List<Booking> bookings) {
        return bookings.stream().map(this::toDto).toList();
    }
}