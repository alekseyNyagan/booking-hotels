package com.aleksey.booking.hotels.mapper;

import com.aleksey.booking.hotels.api.response.RoomInfo;
import com.aleksey.booking.hotels.api.response.RoomResponse;
import com.aleksey.booking.hotels.api.request.UpsertRoomRequest;
import com.aleksey.booking.hotels.model.Room;
import com.aleksey.booking.hotels.model.UnavailableDate;
import org.mapstruct.*;

import java.time.LocalDate;
import java.util.List;

@DecoratedWith(RoomMapperDelegate.class)
@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
public interface RoomMapper {
    Room toEntity(UpsertRoomRequest upsertRoomRequest);

    @Mapping(source = "roomId", target = "id")
    Room toEntity(Long roomId, UpsertRoomRequest upsertRoomRequest);

    RoomResponse toDto(Room room);

    default List<LocalDate> fromUnavailableDatesToLocalDates(List<UnavailableDate> dates) {
        return dates.stream().map(UnavailableDate::getDate).toList();
    }

    List<RoomInfo> roomListToRoomInfoList(List<Room> rooms);
}