package com.aleksey.booking.hotels.mapper;

import com.aleksey.booking.hotels.api.response.RoomResponse;
import com.aleksey.booking.hotels.api.request.UpsertRoomRequest;
import com.aleksey.booking.hotels.model.Room;
import com.aleksey.booking.hotels.model.UnavailableDates;
import org.mapstruct.*;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
public interface RoomMapper {
    Room toEntity(UpsertRoomRequest upsertRoomRequest);

    @Mapping(source = "roomId", target = "id")
    Room toEntity(Long roomId, UpsertRoomRequest upsertRoomRequest);

    RoomResponse toDto(Room room);

    default List<LocalDate> fromUnavailableDatesToLocalDate(List<UnavailableDates> dates) {
        if (dates == null || dates.isEmpty()) {
            return Collections.emptyList();
        }
        return dates.stream().map(UnavailableDates::getDate).toList();
    }
}