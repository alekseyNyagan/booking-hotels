package com.aleksey.booking.hotels.service;

import com.aleksey.booking.hotels.api.request.RoomFilter;
import com.aleksey.booking.hotels.api.request.UpsertRoomRequest;
import com.aleksey.booking.hotels.api.response.RoomPaginationResponse;
import com.aleksey.booking.hotels.api.response.RoomResponse;
import com.aleksey.booking.hotels.mapper.RoomMapper;
import com.aleksey.booking.hotels.model.Room;
import com.aleksey.booking.hotels.repository.RoomRepository;
import com.aleksey.booking.hotels.repository.RoomSpecification;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RoomServiceImpl implements RoomService {

    private final RoomRepository roomRepository;

    private final RoomMapper roomMapper;

    @Override
    public RoomResponse getById(Long id) {
        Optional<Room> room = roomRepository.findById(id);
        return roomMapper.toDto(room.orElseThrow(() -> new EntityNotFoundException(MessageFormat.format("Комната с id {0} не найдена", id))));
    }

    @Override
    public RoomResponse createRoom(UpsertRoomRequest upsertRoomRequest) {
        Room room = roomMapper.toEntity(upsertRoomRequest);
        roomRepository.save(room);
        return roomMapper.toDto(room);
    }

    @Override
    public RoomResponse updateRoom(Long id, UpsertRoomRequest upsertRoomRequest) {
        Room room = roomMapper.toEntity(id, upsertRoomRequest);
        roomRepository.save(room);
        return roomMapper.toDto(room);
    }

    @Override
    public void deleteRoom(Long id) {
        roomRepository.deleteById(id);
    }

    @Override
    public RoomPaginationResponse filterBy(RoomFilter filter) {
        Page<Room> rooms = roomRepository.findAll(new RoomSpecification(filter),
                PageRequest.of(filter.pageNumber(), filter.pageSize()));
        return roomMapper.roomListToRoomPaginationResponse(rooms.getTotalElements(), rooms.getContent());
    }
}