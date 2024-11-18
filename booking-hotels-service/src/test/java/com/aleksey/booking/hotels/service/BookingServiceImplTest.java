package com.aleksey.booking.hotels.service;

import brave.Tracer;
import com.aleksey.booking.hotels.api.request.UpsertBookingRequest;
import com.aleksey.booking.hotels.api.response.BookingPaginationResponse;
import com.aleksey.booking.hotels.api.response.BookingResponse;
import com.aleksey.booking.hotels.api.response.RoomInfo;
import com.aleksey.booking.hotels.exception.RoomsUnavailableException;
import com.aleksey.booking.hotels.mapper.BookingMapper;
import com.aleksey.booking.hotels.model.Booking;
import com.aleksey.booking.hotels.model.Room;
import com.aleksey.booking.hotels.model.UnavailableDate;
import com.aleksey.booking.hotels.repository.BookingRepository;
import com.aleksey.booking.hotels.repository.RoomRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.messaging.Message;
import org.springframework.security.oauth2.jwt.Jwt;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookingServiceImplTest {

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private RoomRepository roomRepository;

    @Mock
    private BookingMapper bookingMapper;

    @Mock
    private StreamBridge streamBridge;

    @Mock
    private Tracer tracer;

    @InjectMocks
    private BookingServiceImpl bookingService;


    /**
     * Given a user with a valid JWT token<br>
     * And a booking request with valid arrival and departure dates<br>
     * And a list of room IDs that are available for the requested dates<br>
     * When the user submits the booking request<br>
     * Then the system should create a new booking<br>
     * And the response should include the booking ID, arrival date, departure date, and room details<br>
     * And the system should send a message to the statistics stream with booking details
     */
    @Test
    void successfulBookingCreationWithValidRequest() {
        UUID userId = UUID.randomUUID();
        Jwt jwt = mock(Jwt.class);
        when(jwt.getSubject()).thenReturn(userId.toString());

        UpsertBookingRequest request = new UpsertBookingRequest(
                "2023-10-01",
                "2023-10-05",
                List.of(1L, 2L)
        );

        Room room1 = new Room();
        room1.setId(1L);
        room1.setUnavailableDates(Collections.emptySet());

        Room room2 = new Room();
        room2.setId(2L);
        room2.setUnavailableDates(Collections.emptySet());

        when(roomRepository.findAllByIdIn(request.roomIds())).thenReturn(List.of(room1, room2));

        Booking booking = new Booking();
        booking.setId(1L);
        booking.setUserId(userId);
        booking.setRooms(List.of(room1, room2));
        booking.setArrivalDate(LocalDate.parse("2023-10-01"));
        booking.setDepartureDate(LocalDate.parse("2023-10-05"));

        when(bookingMapper.toEntity(userId, List.of(room1, room2), LocalDate.parse("2023-10-01"), LocalDate.parse("2023-10-05")))
                .thenReturn(booking);

        BookingResponse bookingResponse = new BookingResponse(
                1L, LocalDate.parse("2023-10-01"),
                LocalDate.parse("2023-10-05"),
                List.of(new RoomInfo(1L, null, null, null, null, null, null),
                        new RoomInfo(2L, null, null, null, null, null, null)),
                userId
        );
        when(bookingMapper.toDto(booking)).thenReturn(bookingResponse);

        BookingResponse result = bookingService.createBooking(request, jwt);

        assertNotNull(result);
        assertEquals(1L, result.id());
        verify(bookingRepository, times(1)).save(booking);
        verify(streamBridge, times(1)).send(anyString(), any(Message.class));
    }

    /**
     * Given a user with a valid JWT token<br>
     * And a booking request with valid arrival and departure dates<br>
     * And a list of room IDs that are not available for the requested dates<br>
     * When the user submits the booking request<br>
     * Then the system should throw a RoomsUnavailableException<br>
     * And the response should indicate that the rooms are already booked for the requested dates
     */
    @Test
    void bookingCreationFailsDueToRoomUnavailability() {
        Jwt jwt = mock(Jwt.class);
        UpsertBookingRequest request = new UpsertBookingRequest("2023-12-01", "2023-12-05", List.of(1L, 2L));
        List<Room> unavailableRooms = List.of(new Room() {{
            setUnavailableDates(Set.of(new UnavailableDate(1L, LocalDate.parse("2023-12-02"))));
        }});

        when(roomRepository.findAllByIdIn(request.roomIds())).thenReturn(unavailableRooms);

        assertThrows(RoomsUnavailableException.class, () -> bookingService.createBooking(request, jwt));
    }

    /**
     * Given a user with a valid JWT token<br>
     * And a request for the first page of bookings with a specified page size<br>
     * When the user requests the booking page<br>
     * Then the system should return a paginated response<br>
     * And the response should include the total number of bookings and a list of booking details for the requested page
     */
    @Test
    void retrievingAPaginatedListOfBookings() {
        int pageSize = 10;
        int pageNumber = 0;
        Page<Booking> bookingsPage = new PageImpl<>(List.of(new Booking()), PageRequest.of(pageNumber, pageSize), 1);
        BookingPaginationResponse expectedResponse = new BookingPaginationResponse(1L, List.of());

        when(bookingRepository.findAll(any(Pageable.class))).thenReturn(bookingsPage);
        when(bookingMapper.bookingListToBookingPaginationResponse(anyLong(), anyList())).thenReturn(expectedResponse);
        BookingPaginationResponse response = bookingService.getBookingPage(pageSize, pageNumber);

        assertEquals(expectedResponse, response);
    }

}