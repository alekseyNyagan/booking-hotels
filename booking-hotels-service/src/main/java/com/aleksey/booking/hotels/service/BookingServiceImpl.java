package com.aleksey.booking.hotels.service;

import brave.Tracer;
import com.aleksey.booking.hotels.api.request.UpsertBookingRequest;
import com.aleksey.booking.hotels.api.response.BookingPaginationResponse;
import com.aleksey.booking.hotels.api.response.BookingResponse;
import com.aleksey.booking.hotels.exception.RoomsUnavailableException;
import com.aleksey.booking.hotels.kafka.model.StatisticModel;
import com.aleksey.booking.hotels.mapper.BookingMapper;
import com.aleksey.booking.hotels.model.Booking;
import com.aleksey.booking.hotels.model.Hotel;
import com.aleksey.booking.hotels.model.Room;
import com.aleksey.booking.hotels.model.UnavailableDate;
import com.aleksey.booking.hotels.repository.BookingRepository;
import com.aleksey.booking.hotels.repository.RoomRepository;
import com.aleksey.booking.hotels.utils.DateConverter;
import lombok.RequiredArgsConstructor;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@EnableTransactionManagement
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;

    private final RoomRepository roomRepository;

    private final BookingMapper bookingMapper;

    private final StreamBridge streamBridge;

    private final Tracer tracer;

    @Override
    @Transactional
    public BookingResponse createBooking(UpsertBookingRequest upsertBookingRequest, Jwt jwt) {
        LocalDate arrivalDate = DateConverter.fromStringDateToLocalDate(upsertBookingRequest.arrivalDate());
        LocalDate departureDate = DateConverter.fromStringDateToLocalDate(upsertBookingRequest.departureDate());
        List<Room> rooms = roomRepository.findAllByIdIn(upsertBookingRequest.roomIds());
        if (rooms.stream().flatMap(room -> room.getUnavailableDates().stream().map(UnavailableDate::getDate))
                .anyMatch(localDate -> arrivalDate.datesUntil(departureDate).toList().contains(localDate))) {
            throw new RoomsUnavailableException("На данную дату, данные комнаты уже забронированы!");
        } else {
            UUID userId = UUID.fromString(jwt.getSubject());
            Booking booking = bookingMapper.toEntity(userId
                    , rooms
                    , arrivalDate
                    , departureDate);
            bookingRepository.save(booking);
            Hotel hotel = rooms.getFirst().getHotel();
            Message<StatisticModel> message = MessageBuilder.withPayload(
                    new StatisticModel(
                            UUID.randomUUID(),
                            userId,
                            booking.getId(),
                            hotel.getId(),
                            hotel.getCity(),
                            rooms.stream().map(Room::getId).toList(),
                            rooms.size(),
                            arrivalDate,
                            departureDate,
                            (int) ChronoUnit.DAYS.between(arrivalDate, departureDate),
                            rooms.stream()
                                    .map(Room::getCost)
                                    .map(BigDecimal::valueOf)
                                    .reduce(BigDecimal.ZERO, BigDecimal::add)
                                    .multiply(BigDecimal.valueOf(ChronoUnit.DAYS.between(arrivalDate, departureDate))),
                            LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))).build();
            streamBridge.send("producer-out-0", message);
            return bookingMapper.toDto(booking);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public BookingPaginationResponse getBookingPage(Integer pageSize, Integer pageNumber) {
        Page<Booking> bookings = bookingRepository.findAll(PageRequest.of(pageNumber, pageSize));
        return bookingMapper.bookingListToBookingPaginationResponse(bookings.getTotalElements(), bookings.getContent());
    }
}