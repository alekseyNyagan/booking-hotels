package com.aleksey.booking.hotels.mapper;

import com.aleksey.booking.hotels.api.request.UpsertRoomRequest;
import com.aleksey.booking.hotels.api.response.RoomInfo;
import com.aleksey.booking.hotels.model.Room;

import java.util.ArrayList;
import java.util.List;

public abstract class RoomMapperDelegate implements RoomMapper {

    @Override
    public Room toEntity(UpsertRoomRequest upsertRoomRequest) {
        Room room = new Room();
        room.setCost(upsertRoomRequest.cost());
        room.setName(upsertRoomRequest.name());
        room.setNumber(upsertRoomRequest.number());
        room.setDescription(upsertRoomRequest.description());
        room.setMaxCountOfPeople(upsertRoomRequest.maxCountOfPeople());
        room.setUnavailableDates(new ArrayList<>());
        return room;
    }

    @Override
    public Room toEntity(Long id, UpsertRoomRequest upsertRoomRequest) {
        Room room = toEntity(upsertRoomRequest);
        room.setId(id);
        return room;
    }

    @Override
    public List<RoomInfo> roomListToRoomInfoList(List<Room> rooms) {
        return rooms.stream().map(room -> new RoomInfo(
                room.getId()
                , room.getName()
                , room.getDescription()
                , room.getNumber()
                , room.getCost()
                , room.getMaxCountOfPeople())).toList();
    }
}