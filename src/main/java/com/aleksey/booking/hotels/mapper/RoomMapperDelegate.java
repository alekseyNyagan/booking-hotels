package com.aleksey.booking.hotels.mapper;

import com.aleksey.booking.hotels.api.request.UpsertRoomRequest;
import com.aleksey.booking.hotels.api.response.RoomInfo;
import com.aleksey.booking.hotels.api.response.RoomResponse;
import com.aleksey.booking.hotels.model.Room;
import com.aleksey.booking.hotels.repository.HotelRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

public abstract class RoomMapperDelegate implements RoomMapper {

    @Autowired
    private HotelRepository hotelRepository;

    @Override
    public Room toEntity(UpsertRoomRequest upsertRoomRequest) {
        Room room = new Room();
        room.setCost(upsertRoomRequest.cost());
        room.setName(upsertRoomRequest.name());
        room.setNumber(upsertRoomRequest.number());
        room.setDescription(upsertRoomRequest.description());
        room.setMaxCountOfPeople(upsertRoomRequest.maxCountOfPeople());
        room.setUnavailableDates(new ArrayList<>());
        room.setHotel(hotelRepository.findById(upsertRoomRequest.hotelId()).orElseThrow(() ->
                new EntityNotFoundException("Отель с id " + upsertRoomRequest.hotelId() + " не найден!")));
        return room;
    }

    @Override
    public Room toEntity(Long id, UpsertRoomRequest upsertRoomRequest) {
        Room room = toEntity(upsertRoomRequest);
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