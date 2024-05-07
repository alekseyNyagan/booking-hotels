package com.aleksey.booking.hotels.service;

import com.aleksey.booking.hotels.api.request.UpsertRoomRequest;
import com.aleksey.booking.hotels.api.response.RoomResponse;

public interface RoomService {

    RoomResponse getById(Long id);

    RoomResponse createRoom(UpsertRoomRequest upsertRoomRequest);

    RoomResponse updateRoom(Long id, UpsertRoomRequest upsertRoomRequest);

    void deleteRoom(Long id);
}