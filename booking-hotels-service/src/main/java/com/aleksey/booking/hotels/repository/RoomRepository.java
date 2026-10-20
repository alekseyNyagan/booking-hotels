package com.aleksey.booking.hotels.repository;

import com.aleksey.booking.hotels.model.Room;
import com.aleksey.booking.hotels.repository.projection.RoomProjection;
import jakarta.persistence.LockModeType;
import jakarta.persistence.QueryHint;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface RoomRepository extends JpaRepository<Room, Long>, JpaSpecificationExecutor<Room> {

    @Override
    @EntityGraph(attributePaths = {"unavailableDates"})
    Optional<Room> findById(Long aLong);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @QueryHints({@QueryHint(name="jakarta.persistence.lock.timeout", value="3000")})
    @EntityGraph(attributePaths = {"unavailableDates"})
    List<Room> findAllByIdIn(List<Long> ids);

    @Modifying
    @Query(value = """
        DELETE FROM room2date
        WHERE room_id IN :roomIds
            AND date_id IN (
                        SELECT id FROM unavailable_dates WHERE date >= :arrival AND date <= :departure
                    )
        """, nativeQuery = true)
    void unlinkRoomDates(@Param("roomIds") List<Long> roomIds,
                         @Param("arrival") LocalDate arrival,
                         @Param("departure") LocalDate departure);

    @Query(value = "SELECT new com.aleksey.booking.hotels.repository.projection.RoomProjection(r.id, r.name, r.description, r.number, r.cost, r.maxCountOfPeople, r.hotel.id, r.booking.id) FROM Room r WHERE r.booking.id IN :bookingIds")
    List<RoomProjection> findRoomsByBookingIds(@Param("bookingIds") Collection<Long> bookingIds);
}