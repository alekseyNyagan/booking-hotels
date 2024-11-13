package com.aleksey.booking.hotels.service;

import com.aleksey.booking.hotels.api.request.RoomFilter;
import com.aleksey.booking.hotels.api.request.UpsertRoomRequest;
import com.aleksey.booking.hotels.api.response.RoomPaginationResponse;
import com.aleksey.booking.hotels.api.response.RoomResponse;
import com.aleksey.booking.hotels.mapper.RoomMapper;
import com.aleksey.booking.hotels.model.Hotel;
import com.aleksey.booking.hotels.model.Room;
import com.aleksey.booking.hotels.repository.RoomRepository;
import com.aleksey.booking.hotels.repository.RoomSpecification;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RoomServiceImplTest {

    private RoomServiceImpl roomService;
    private RoomRepository roomRepository;
    private RoomMapper roomMapper;
    private Hotel hotel;

    @BeforeEach
    void setUp() {
        roomRepository = Mockito.mock(RoomRepository.class);
        roomMapper = Mockito.mock(RoomMapper.class);
        roomService = new RoomServiceImpl(roomRepository, roomMapper);
        hotel = new Hotel();
        hotel.setId(1L);
        hotel.setName("Test Hotel");
        hotel.setTitle("A great place to stay");
        hotel.setCity("Test City");
        hotel.setAddress("123 Test St");
    }

    /**
     * Given a room exists with a valid ID<br>
     * When a request is made to retrieve the room details using the valid ID<br>
     * Then the system should return the room details including name, description, number, cost, and maximum capacity<br>
     * And the response should indicate a successful retrieval of the room information
     */
    @Test
    void retrieveRoomDetailsByValidId() {
        Long roomId = 1L;
        Room room = new Room();
        room.setId(roomId);
        room.setName("Deluxe Room");
        room.setDescription("A spacious room");
        room.setNumber("101");
        room.setCost(100);
        room.setMaxCountOfPeople(2);
        room.setHotel(hotel);
        RoomResponse roomResponse = new RoomResponse(roomId, "Deluxe Room", "A spacious room", "101", 100, 2, new HashSet<>(), 1L);

        when(roomRepository.findById(roomId)).thenReturn(Optional.of(room));
        when(roomMapper.toDto(room)).thenReturn(roomResponse);

        RoomResponse response = roomService.getById(roomId);

        assertEquals(roomResponse, response);
        verify(roomRepository).findById(roomId);
    }

    /**
     * Given no room exists with the specified invalid ID<br>
     * When a request is made to retrieve the room details using the invalid ID<br>
     * Then the system should throw an error indicating that the room was not found<br>
     * And the error message should specify the invalid ID used in the request
     */
    @Test
    void attemptToRetrieveRoomDetailsByInvalidId() {
        Long invalidRoomId = 999L;

        when(roomRepository.findById(invalidRoomId)).thenReturn(Optional.empty());

        Assertions.assertThrows(EntityNotFoundException.class, () -> roomService.getById(invalidRoomId));
        verify(roomRepository).findById(invalidRoomId);
    }

    /**
     * Given a request to create a new room with valid details<br>
     * When the room creation request is processed<br>
     * Then the system should save the new room in the database<br>
     * And the response should include the details of the newly created room<br>
     * And the response should indicate a successful creation of the room
     */
    @Test
    void createANewRoomWithValidDetails() {
        UpsertRoomRequest request = new UpsertRoomRequest("Standard Room", "A cozy room", "102", 80, 2, 1L);
        Room room = new Room();
        room.setId(null);
        room.setName("Standard Room");
        room.setDescription("A cozy room");
        room.setNumber("102");
        room.setCost(80);
        room.setMaxCountOfPeople(2);
        room.setHotel(hotel);
        RoomResponse roomResponse = new RoomResponse(1L, "Standard Room", "A cozy room", "102", 80, 2, new HashSet<>(), 1L);

        when(roomMapper.toEntity(request)).thenReturn(room);
        when(roomRepository.save(room)).thenReturn(room);
        when(roomMapper.toDto(room)).thenReturn(roomResponse);

        RoomResponse response = roomService.createRoom(request);

        assertEquals(roomResponse, response);
        verify(roomRepository).save(room);
    }

    /**
     * Given a room exists with a valid ID<br>
     * And a request is made to update the room with new valid details<br>
     * When the update request is processed<br>
     * Then the system should update the room details in the database<br>
     * And the response should include the updated room details<br>
     * And the response should indicate a successful update of the room
     */
    @Test
    void updateAnExistingRoomWithValidDetails() {
        Long roomId = 1L;
        UpsertRoomRequest request = new UpsertRoomRequest("Updated Room", "An updated description", "103", 90, 3, 1L);
        Room room = new Room();
        room.setId(roomId);
        room.setName("Updated Room");
        room.setDescription("An updated description");
        room.setNumber("103");
        room.setCost(90);
        room.setMaxCountOfPeople(3);
        room.setHotel(hotel);
        RoomResponse roomResponse = new RoomResponse(roomId, "Updated Room", "An updated description", "103", 90, 3, new HashSet<>(), 1L);

        when(roomMapper.toEntity(roomId, request)).thenReturn(room);
        when(roomRepository.save(room)).thenReturn(room);
        when(roomMapper.toDto(room)).thenReturn(roomResponse);

        RoomResponse response = roomService.updateRoom(roomId, request);

        assertEquals(roomResponse, response);
        verify(roomRepository).save(room);
    }

    /**
     * Given a room exists with a valid ID<br>
     * When a request is made to delete the room using the valid ID<br>
     * Then the system should remove the room from the database<br>
     * And the response should indicate a successful deletion of the room<br>
     * And subsequent requests to retrieve the room should indicate that it no longer exists
     */
    @Test
    void deleteRoomByValidId() {
        Long roomId = 1L;

        roomService.deleteRoom(roomId);

        verify(roomRepository).deleteById(roomId);
    }

    /**
     * Given a request to filter rooms with a specified hotel ID<br>
     * And the hotel ID is valid and exists in the system<br>
     * And the cost range is defined with a minimum and maximum value<br>
     * When the filter is applied to the room repository<br>
     * Then the system should return a list of rooms that belong to the specified hotel<br>
     * And all returned rooms should have costs within the defined range<br>
     * And the total number of rooms returned should be accurate based on the filter criteria
     */
    @Test
    void filterRoomsByValidHotelIdAndCostRange() {
        RoomFilter filter = new RoomFilter(10, 0, null, null, 50, 150, null, null, null, 1L);
        Room room1 = new Room(1L, "Room A", "Description A", "101", 100, 2, new HashSet<>(), hotel, null);
        Room room2 = new Room(2L, "Room B", "Description B", "102", 120, 2, new HashSet<>(), hotel, null);
        List<Room> rooms = Arrays.asList(room1, room2);
        RoomPaginationResponse expectedResponse = new RoomPaginationResponse(2L, Arrays.asList(
                new RoomResponse(1L, "Room A", "Description A", "101", 100, 2, new HashSet<>(), 1L),
                new RoomResponse(2L, "Room B", "Description B", "102", 120, 2, new HashSet<>(), 1L)
        ));

        doReturn(new PageImpl<>(rooms, PageRequest.of(0, 10), rooms.size()))
                .when(roomRepository).findAll(any(RoomSpecification.class), any(PageRequest.class));
        doReturn(expectedResponse).when(roomMapper).roomListToRoomPaginationResponse(anyLong(), anyList());

        RoomPaginationResponse response = roomService.filterBy(filter);

        assertEquals(expectedResponse, response);
        verify(roomRepository).findAll(any(RoomSpecification.class), any(PageRequest.class));
    }

    /**
     * Given a request to filter rooms with a specified count of visitors<br>
     * And the count of visitors is within the acceptable range<br>
     * And the arrival and departure dates are provided<br>
     * When the filter is applied to the room repository<br>
     * Then the system should return a list of rooms that can accommodate the specified number of visitors<br>
     * And the returned rooms should not have any unavailable dates overlapping with the specified dates<br>
     * And the total number of rooms returned should reflect the filtering criteria
     */
    @Test
    void filterRoomsByVisitorCountAndUnavailableDates() {
        RoomFilter filter = new RoomFilter(10, 0, null, null, null, null, 2, "2024-11-01", "2024-11-05", 1L);
        Room room = new Room(1L, "Room A", "Description A", "101", 100, 2, new HashSet<>(), hotel, null);
        List<Room> rooms = Collections.singletonList(room);
        RoomPaginationResponse expectedResponse = new RoomPaginationResponse(1L, Collections.singletonList(
                new RoomResponse(1L, "Room A", "Description A", "101", 100, 2, new HashSet<>(), 1L)
        ));

        when(roomRepository.findAll(any(RoomSpecification.class), any(Pageable.class))).thenReturn(new PageImpl<>(rooms));
        when(roomMapper.roomListToRoomPaginationResponse(1L, rooms)).thenReturn(expectedResponse);

        RoomPaginationResponse response = roomService.filterBy(filter);

        assertEquals(expectedResponse, response);
        verify(roomRepository).findAll(any(RoomSpecification.class), any(Pageable.class));
    }

    /**
     * Given a request to filter rooms with no specific criteria<br>
     * When the filter is applied to the room repository<br>
     * Then the system should return all available rooms<br>
     * And the total number of rooms returned should match the total count of rooms in the system
     */
    @Test
    void filterRoomsWithNoCriteriaSpecified() {
        RoomFilter filter = new RoomFilter(10, 0, null, null, null, null, null, null, null, 1L);
        Room room1 = new Room(1L, "Room A", "Description A", "101", 100, 2, new HashSet<>(), hotel, null);
        Room room2 = new Room(2L, "Room B", "Description B", "102", 150, 3, new HashSet<>(), hotel, null);
        List<Room> rooms = Arrays.asList(room1, room2);
        RoomPaginationResponse expectedResponse = new RoomPaginationResponse(2L, Arrays.asList(
                new RoomResponse(1L, "Room A", "Description A", "101", 100, 2, new HashSet<>(), 1L),
                new RoomResponse(2L, "Room B", "Description B", "102", 150, 3, new HashSet<>(), 1L)
        ));

        when(roomRepository.findAll(any(RoomSpecification.class), any(Pageable.class))).thenReturn(new PageImpl<>(rooms));
        when(roomMapper.roomListToRoomPaginationResponse(2L, rooms)).thenReturn(expectedResponse);

        RoomPaginationResponse response = roomService.filterBy(filter);

        assertEquals(expectedResponse, response);
        verify(roomRepository).findAll(any(RoomSpecification.class), any(Pageable.class));
    }

    /**
     * Given a request to filter rooms with a specified name<br>
     * And the hotel ID is valid and exists in the system<br>
     * When the filter is applied to the room repository<br>
     * Then the system should return a list of rooms that match the specified name<br>
     * And all returned rooms should belong to the specified hotel<br>
     * And the total number of rooms returned should be accurate based on the filter criteria
     */
    @Test
    void filterRoomsByNameAndHotelId() {
        RoomFilter filter = new RoomFilter(10, 0, null, "Room A", null, null, null, null, null, 1L);
        Room room = new Room(1L, "Room A", "Description A", "101", 100, 2, new HashSet<>(), hotel, null);
        List<Room> rooms = Collections.singletonList(room);
        RoomPaginationResponse expectedResponse = new RoomPaginationResponse(1L, Collections.singletonList(
                new RoomResponse(1L, "Room A", "Description A", "101", 100, 2, new HashSet<>(), 1L)
        ));

        when(roomRepository.findAll(any(RoomSpecification.class), any(Pageable.class))).thenReturn(new PageImpl<>(rooms));
        when(roomMapper.roomListToRoomPaginationResponse(1L, rooms)).thenReturn(expectedResponse);

        RoomPaginationResponse response = roomService.filterBy(filter);

        assertEquals(expectedResponse, response);
        verify(roomRepository).findAll(any(RoomSpecification.class), any(Pageable.class));
    }

    /**
     * Given a request to filter rooms with an invalid hotel ID<br>
     * When the filter is applied to the room repository<br>
     * Then the system should return an empty list of rooms<br>
     * And an appropriate message indicating that no rooms were found should be generated
     */
    @Test
    void filterRoomsWithInvalidHotelId() {
        RoomFilter filter = new RoomFilter(10, 0, null, null, null, null, null, null, null, 999L);
        List<Room> rooms = Collections.emptyList();
        List<RoomResponse> roomResponses = Collections.emptyList();
        RoomPaginationResponse expectedResponse = new RoomPaginationResponse(0L, roomResponses);

        when(roomRepository.findAll(any(RoomSpecification.class), any(Pageable.class))).thenReturn(new PageImpl<>(rooms));
        when(roomMapper.roomListToRoomPaginationResponse(0L, rooms)).thenReturn(expectedResponse);

        RoomPaginationResponse response = roomService.filterBy(filter);

        assertEquals(expectedResponse, response);
        verify(roomRepository).findAll(any(RoomSpecification.class), any(Pageable.class));
    }

}
