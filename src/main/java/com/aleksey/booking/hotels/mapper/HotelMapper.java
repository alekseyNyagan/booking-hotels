package com.aleksey.booking.hotels.mapper;

import com.aleksey.booking.hotels.api.request.UpsertHotelRequest;
import com.aleksey.booking.hotels.api.response.HotelListResponse;
import com.aleksey.booking.hotels.api.response.HotelResponse;
import com.aleksey.booking.hotels.model.Hotel;
import org.mapstruct.*;

import java.util.List;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
public interface HotelMapper {
    Hotel toEntity(UpsertHotelRequest upsertHotelRequest);

    @Mapping(source = "hotelId", target = "id")
    Hotel toEntity(Long hotelId, UpsertHotelRequest upsertHotelRequest);

    HotelResponse toDto(Hotel hotel);

    List<HotelResponse> hotelListToResponseList(List<Hotel> hotels);

    default HotelListResponse hotelListToHotelListResponse(List<Hotel> hotels) {
        return new HotelListResponse(hotelListToResponseList(hotels));
    }
}