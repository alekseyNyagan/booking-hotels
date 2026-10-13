package com.aleksey.booking.hotels.scheduler;

import com.aleksey.booking.hotels.model.Booking;
import com.aleksey.booking.hotels.model.Room;
import com.aleksey.booking.hotels.model.enums.BookingStatus;
import com.aleksey.booking.hotels.repository.BookingRepository;
import com.aleksey.booking.hotels.repository.RoomRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class BookingPaymentScheduler {

    private final BookingRepository bookingRepository;
    private final RoomRepository roomRepository;

    @Scheduled(fixedRate = 60000)
    @Transactional
    public void reconcilePendingPayments() {
        log.info("Запуск планировщика проверки консистентности распределенных транзакций...");

        LocalDateTime thresholdTime = LocalDateTime.now().minusMinutes(10);

        List<Booking> stuckBookings = bookingRepository.findAllByStatusAndCreatedAtBefore(
                BookingStatus.PENDING_PAYMENT, thresholdTime
        );

        if (stuckBookings.isEmpty()) {
            log.info("✅ Зависших бронирований не обнаружено. Система в полной консистентности.");
            return;
        }

        log.warn("⚠️ Обнаружено {} зависших бронирований. Начинаем сверку с платежным сервисом...", stuckBookings.size());

        for (Booking booking : stuckBookings) {
            try {
                log.info("❌ Платеж по брони {} не подтвержден шлюзом в отведенное время. Аннулируем бронирование.", booking.getId());

                List<Long> roomIds = booking.getRooms().stream().map(Room::getId).toList();

                roomRepository.unlinkRoomDates(roomIds, booking.getArrivalDate(), booking.getDepartureDate());

                booking.setStatus(BookingStatus.CANCELLED);
                bookingRepository.save(booking);
            } catch (Exception e) {
                log.error("❌ Не удалось сверить статус для бронирования {}", booking.getId(), e);
            }
        }
    }
}
