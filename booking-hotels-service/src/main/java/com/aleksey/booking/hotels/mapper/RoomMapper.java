package com.aleksey.booking.hotels.mapper;

import com.aleksey.booking.hotels.api.response.RoomInfo;
import com.aleksey.booking.hotels.api.response.RoomPaginationResponse;
import com.aleksey.booking.hotels.api.response.RoomResponse;
import com.aleksey.booking.hotels.api.request.UpsertRoomRequest;
import com.aleksey.booking.hotels.model.Hotel;
import com.aleksey.booking.hotels.model.Room;
import com.aleksey.booking.hotels.model.UnavailableDate;
import org.mapstruct.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@DecoratedWith(RoomMapperDelegate.class)
@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
public interface RoomMapper {
    @Mapping(source = "upsertRoomRequest.name", target = "name")
    Room toEntity(UpsertRoomRequest upsertRoomRequest, Hotel hotel);

    @Mapping(source = "roomId", target = "id")
    @Mapping(source = "upsertRoomRequest.name", target = "name")
    Room toEntity(Long roomId, UpsertRoomRequest upsertRoomRequest, Hotel hotel);

    RoomResponse toDto(Room room);

    default Set<LocalDate> fromUnavailableDatesToLocalDates(Set<UnavailableDate> dates) {
        return dates.stream().map(UnavailableDate::getDate).collect(Collectors.toSet());
    }

    List<RoomInfo> roomListToRoomInfoList(List<Room> rooms);

    List<RoomResponse> roomListToResponseList(List<Room> rooms);

    default RoomPaginationResponse roomListToRoomPaginationResponse(Long roomsCount, List<Room> rooms) {
        return new RoomPaginationResponse(roomsCount, roomListToResponseList(rooms));
    }
}