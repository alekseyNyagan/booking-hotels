package com.aleksey.booking.hotels.mapper;

import com.aleksey.booking.hotels.api.request.UpsertRoomRequest;
import com.aleksey.booking.hotels.api.response.RoomInfo;
import com.aleksey.booking.hotels.api.response.RoomResponse;
import com.aleksey.booking.hotels.model.Hotel;
import com.aleksey.booking.hotels.model.Room;

import java.util.HashSet;
import java.util.List;

public abstract class RoomMapperDelegate implements RoomMapper {

    @Override
    public Room toEntity(UpsertRoomRequest upsertRoomRequest, Hotel hotel) {
        Room room = new Room();
        room.setCost(upsertRoomRequest.cost());
        room.setName(upsertRoomRequest.name());
        room.setNumber(upsertRoomRequest.number());
        room.setDescription(upsertRoomRequest.description());
        room.setMaxCountOfPeople(upsertRoomRequest.maxCountOfPeople());
        room.setUnavailableDates(new HashSet<>());
        room.setHotel(hotel);
        return room;
    }

    @Override
    public Room toEntity(Long id, UpsertRoomRequest upsertRoomRequest, Hotel hotel) {
        Room room = toEntity(upsertRoomRequest, hotel);
        room.setId(id);
        return room;
    }

    @Override
    public RoomResponse toDto(Room room) {
        return new RoomResponse(
                room.getId()
                , room.getName()
                , room.getDescription()
                , room.getNumber()
                , room.getCost()
                , room.getMaxCountOfPeople()
                , fromUnavailableDatesToLocalDates(room.getUnavailableDates())
                , room.getHotel().getId()
        );
    }

    @Override
    public List<RoomInfo> roomListToRoomInfoList(List<Room> rooms) {
        return rooms.stream().map(room -> new RoomInfo(
                room.getId()
                , room.getName()
                , room.getDescription()
                , room.getNumber()
                , room.getCost()
                , room.getMaxCountOfPeople()
                , room.getHotel().getId())).toList();
    }

    @Override
    public List<RoomResponse> roomListToResponseList(List<Room> rooms) {
        return rooms.stream().map(this::toDto).toList();
    }
}