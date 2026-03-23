package com.aleksey.booking.hotels.service;

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
public class BookingServiceImpl implements BookingService {

    private static final String STATISTIC_DATETIME_FORMAT = "yyyy-MM-dd HH:mm:ss";
    private static final DateTimeFormatter STATISTIC_DATETIME_FORMATTER = DateTimeFormatter.ofPattern(STATISTIC_DATETIME_FORMAT);
    private static final String KAFKA_PRODUCER_BINDING = "producer-out-0";

    private final BookingRepository bookingRepository;

    private final RoomRepository roomRepository;

    private final BookingMapper bookingMapper;

    private final StreamBridge streamBridge;

    @Override
    @Transactional
    public BookingResponse createBooking(UpsertBookingRequest upsertBookingRequest, Jwt jwt) {
        LocalDate arrivalDate = DateConverter.fromStringDateToLocalDate(upsertBookingRequest.arrivalDate());
        LocalDate departureDate = DateConverter.fromStringDateToLocalDate(upsertBookingRequest.departureDate());
        List<Room> rooms = roomRepository.findAllByIdIn(upsertBookingRequest.roomIds());

        validateRoomsAvailability(rooms, arrivalDate, departureDate);

        UUID userId = UUID.fromString(jwt.getSubject());
        Booking booking = bookingMapper.toEntity(userId, rooms, arrivalDate, departureDate);
        bookingRepository.save(booking);

        streamBridge.send(KAFKA_PRODUCER_BINDING, buildStatisticMessage(booking, rooms, userId, arrivalDate, departureDate));
        return bookingMapper.toDto(booking);
    }

    @Override
    @Transactional(readOnly = true)
    public BookingPaginationResponse getBookingPage(Integer pageSize, Integer pageNumber) {
        Page<Booking> bookings = bookingRepository.findAll(PageRequest.of(pageNumber, pageSize));
        return bookingMapper.bookingListToBookingPaginationResponse(bookings.getTotalElements(), bookings.getContent());
    }

    private void validateRoomsAvailability(List<Room> rooms, LocalDate arrivalDate, LocalDate departureDate) {
        List<LocalDate> requestedDates = arrivalDate.datesUntil(departureDate).toList();
        boolean hasConflict = rooms.stream()
                .flatMap(room -> room.getUnavailableDates().stream().map(UnavailableDate::getDate))
                .anyMatch(requestedDates::contains);
        if (hasConflict) {
            throw new RoomsUnavailableException("На данную дату, данные комнаты уже забронированы!");
        }
    }

    private Message<StatisticModel> buildStatisticMessage(Booking booking, List<Room> rooms,
                                                          UUID userId, LocalDate arrivalDate, LocalDate departureDate) {
        Hotel hotel = rooms.getFirst().getHotel();
        long nights = ChronoUnit.DAYS.between(arrivalDate, departureDate);
        BigDecimal totalCost = rooms.stream()
                .map(Room::getCost)
                .map(BigDecimal::valueOf)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .multiply(BigDecimal.valueOf(nights));

        StatisticModel payload = new StatisticModel(
                UUID.randomUUID(),
                userId,
                booking.getId(),
                hotel.getId(),
                hotel.getCity(),
                rooms.stream().map(Room::getId).toList(),
                rooms.size(),
                arrivalDate,
                departureDate,
                (int) nights,
                totalCost,
                LocalDateTime.now().format(STATISTIC_DATETIME_FORMATTER));
        return MessageBuilder.withPayload(payload).build();
    }
}