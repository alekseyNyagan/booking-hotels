package com.aleksey.booking.hotels.repository;

import com.aleksey.booking.hotels.model.Room;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

class RoomRepositoryTest extends BaseRepositoryTest {

    @Autowired
    private RoomRepository roomRepository;

    @BeforeEach
    void setUp() {
        roomRepository.deleteAll();
    }

    @Test
    void testFindById() {
        Room room = new Room();
        room.setName("Room 1");
        roomRepository.save(room);

        Optional<Room> foundRoom = roomRepository.findById(room.getId());

        assertThat(foundRoom).isPresent();
        assertThat(foundRoom.get().getName()).isEqualTo("Room 1");
    }

    @Test
    void testFindAllByIdIn() {
        Room room1 = new Room();
        room1.setName("Room 1");
        roomRepository.save(room1);

        Room room2 = new Room();
        room2.setName("Room 2");
        roomRepository.save(room2);

        List<Room> foundRooms = roomRepository.findAllByIdIn(List.of(room1.getId(), room2.getId()));

        assertThat(foundRooms).hasSize(2);
        assertThat(foundRooms).extracting(Room::getName).containsExactlyInAnyOrder("Room 1", "Room 2");
    }

    @Test
    void testFindAll() {
        Room room1 = new Room();
        room1.setName("Room 1");
        roomRepository.save(room1);

        Room room2 = new Room();
        room2.setName("Room 2");
        roomRepository.save(room2);

        Specification<Room> spec = (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("name"), "Room 1");
        Pageable pageable = PageRequest.of(0, 10);

        Page<Room> foundRooms = roomRepository.findAll(spec, pageable);

        assertThat(foundRooms.getContent()).hasSize(1);
        assertThat(foundRooms.getContent().getFirst().getName()).isEqualTo("Room 1");
    }
}