package com.aleksey.booking.hotels.service;

import com.aleksey.booking.hotels.api.response.BookingPaginationResponse;
import com.aleksey.booking.hotels.api.response.BookingResponse;
import com.aleksey.booking.hotels.api.request.UpsertBookingRequest;
import com.aleksey.booking.hotels.exception.RoomsUnavailableException;
import com.aleksey.booking.hotels.mapper.BookingMapper;
import com.aleksey.booking.hotels.model.Booking;
import com.aleksey.booking.hotels.model.Room;
import com.aleksey.booking.hotels.model.UnavailableDate;
import com.aleksey.booking.hotels.repository.BookingRepository;
import com.aleksey.booking.hotels.repository.RoomRepository;
import com.aleksey.booking.hotels.utils.DateConverter;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;

    private final RoomRepository roomRepository;

    private final BookingMapper bookingMapper;

    @Override
    public BookingResponse createBooking(UpsertBookingRequest upsertBookingRequest, Jwt jwt) {
        LocalDate arrivalDate = DateConverter.fromStringDateToLocalDate(upsertBookingRequest.arrivalDate());
        LocalDate departureDate = DateConverter.fromStringDateToLocalDate(upsertBookingRequest.departureDate());
        List<Room> rooms = roomRepository.findAllByIdIn(upsertBookingRequest.roomIds());
        if (rooms.stream().flatMap(room -> room.getUnavailableDates().stream().map(UnavailableDate::getDate))
                .anyMatch(localDate -> arrivalDate.datesUntil(departureDate).toList().contains(localDate))) {
            throw new RoomsUnavailableException("На данную дату, данные комнаты уже забронированы!");
        } else {
            Booking booking = bookingMapper.toEntity(UUID.fromString(jwt.getClaim("sub"))
                    , rooms
                    , arrivalDate
                    , departureDate);
            roomRepository.saveAll(booking.getRooms());
            bookingRepository.save(booking);
            return bookingMapper.toDto(booking);
        }
    }

    @Override
    public BookingPaginationResponse getBookingPage(Integer pageSize, Integer pageNumber) {
        Page<Booking> bookings = bookingRepository.findAll(PageRequest.of(pageNumber, pageSize));
        return bookingMapper.bookingListToBookingPaginationResponse(bookings.getTotalElements(), bookings.getContent());
    }
}