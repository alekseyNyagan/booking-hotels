package com.aleksey.booking.hotels.mapper;

import com.aleksey.booking.hotels.api.response.BookingResponse;
import com.aleksey.booking.hotels.model.Booking;
import com.aleksey.booking.hotels.model.Room;
import com.aleksey.booking.hotels.model.UnavailableDate;
import com.aleksey.booking.hotels.model.User;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.util.List;

public abstract class BookingMapperDelegate implements BookingMapper {

    @Autowired
    private RoomMapper roomMapper;

    @Autowired
    private UserMapper userMapper;

    @Override
    public Booking toEntity(User user, List<Room> rooms, LocalDate arrivalDate, LocalDate departureDate) {
        List<UnavailableDate> unavailableDates = arrivalDate.datesUntil(departureDate).map(localDate -> {
            UnavailableDate unavailableDate = new UnavailableDate();
            unavailableDate.setDate(localDate);
            return unavailableDate;
        }).toList();
        rooms.forEach(room -> room.getUnavailableDates().addAll(unavailableDates));
        Booking booking = new Booking();
        booking.setArrivalDate(arrivalDate);
        booking.setDepartureDate(departureDate);
        booking.setRooms(rooms);
        booking.setUser(user);
        return booking;
    }

    @Override
    public BookingResponse toDto(Booking booking) {
        return new BookingResponse(
                booking.getId()
                , booking.getArrivalDate()
                , booking.getDepartureDate()
                , roomMapper.roomListToRoomInfoList(booking.getRooms())
                , userMapper.toDto(booking.getUser()));
    }

    @Override
    public List<BookingResponse> bookingListToResponseList(List<Booking> bookings) {
        return bookings.stream().map(this::toDto).toList();
    }
}