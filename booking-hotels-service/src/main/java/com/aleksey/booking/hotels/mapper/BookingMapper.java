package com.aleksey.booking.hotels.mapper;

import com.aleksey.booking.hotels.api.response.BookingResponse;
import com.aleksey.booking.hotels.model.Booking;
import com.aleksey.booking.hotels.repository.projection.BookingProjection;
import com.aleksey.booking.hotels.repository.projection.RoomProjection;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING, uses = RoomMapper.class)
public interface BookingMapper {

    @Mapping(source = "rooms", target = "rooms")
    BookingResponse toDto(Booking booking);

    BookingResponse toResponse(BookingProjection booking, List<RoomProjection> rooms);
}