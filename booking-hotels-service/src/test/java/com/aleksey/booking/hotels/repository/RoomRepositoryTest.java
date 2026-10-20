package com.aleksey.booking.hotels.repository;

import com.aleksey.booking.hotels.model.Booking;
import com.aleksey.booking.hotels.model.Hotel;
import com.aleksey.booking.hotels.model.Room;
import com.aleksey.booking.hotels.repository.projection.RoomProjection;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class RoomRepositoryTest extends BaseRepositoryTest {

    @Autowired
    private RoomRepository roomRepository;

    @Autowired
    BookingRepository bookingRepository;

    @Autowired
    HotelRepository hotelRepository;

    @Autowired
    JdbcTemplate jdbcTemplate;

    @BeforeEach
    void setUp() {
        bookingRepository.deleteAll();
        roomRepository.deleteAll();
        hotelRepository.deleteAll();
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
    void shouldCorrectlyLoadRoomProjectionsByBookingIdsWithHotelNavigation() {
        Hotel hotel = new Hotel();
        hotel.setName("Test Hotel");
        hotelRepository.save(hotel);

        Booking booking = new Booking();
        bookingRepository.save(booking);

        Room room = new Room();
        room.setName("Premium Suite");
        room.setHotel(hotel);
        room.setBooking(booking);
        roomRepository.save(room);

        List<RoomProjection> result = roomRepository.findRoomsByBookingIds(List.of(booking.getId()));

        assertThat(result).hasSize(1);
        RoomProjection projection = result.getFirst();
        assertThat(projection.name()).isEqualTo("Premium Suite");
        assertThat(projection.hotelId()).isEqualTo(hotel.getId());
        assertThat(projection.bookingId()).isEqualTo(booking.getId());
    }

    @Test
    void shouldPhysicalDeleteJoinTableRowsOnUnlinkRoomDates() {
        Room room = new Room();
        room.setName("Room to Unlink");
        roomRepository.save(room);

        LocalDate arrival = LocalDate.of(2026, 10, 20);
        LocalDate departure = LocalDate.of(2026, 10, 22);

        roomRepository.unlinkRoomDates(List.of(room.getId()), arrival, departure);

        Integer count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM room2date WHERE room_id = ?",
                Integer.class,
                room.getId()
        );
        assertThat(count).isZero();

    }
}