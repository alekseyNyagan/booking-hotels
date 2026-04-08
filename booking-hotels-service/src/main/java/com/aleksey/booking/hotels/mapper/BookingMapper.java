package com.aleksey.booking.hotels.mapper;

import com.aleksey.booking.hotels.api.response.BookingPaginationResponse;
import com.aleksey.booking.hotels.api.response.BookingResponse;
import com.aleksey.booking.hotels.model.Booking;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING, uses = RoomMapper.class)
public interface BookingMapper {

    @Mapping(source = "rooms", target = "rooms")
    BookingResponse toDto(Booking booking);

    List<BookingResponse> bookingListToResponseList(List<Booking> bookings);

    default BookingPaginationResponse bookingListToBookingPaginationResponse(Long bookingsCount, List<Booking> bookings)  {
        return new BookingPaginationResponse(bookingsCount, bookingListToResponseList(bookings));
    }
}