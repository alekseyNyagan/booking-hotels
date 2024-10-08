package com.aleksey.booking.hotels.mapper;

import com.aleksey.booking.hotels.api.response.BookingPaginationResponse;
import com.aleksey.booking.hotels.api.response.BookingResponse;
import com.aleksey.booking.hotels.model.Booking;
import com.aleksey.booking.hotels.model.Room;
import org.mapstruct.*;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@DecoratedWith(BookingMapperDelegate.class)
@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
public interface BookingMapper {
    Booking toEntity(UUID userId, List<Room> rooms, LocalDate arrivalDate, LocalDate departureDate);

    BookingResponse toDto(Booking booking);

    List<BookingResponse> bookingListToResponseList(List<Booking> bookings);

    default BookingPaginationResponse bookingListToBookingPaginationResponse(Long bookingsCount, List<Booking> bookings)  {
        return new BookingPaginationResponse(bookingsCount, bookingListToResponseList(bookings));
    }
}