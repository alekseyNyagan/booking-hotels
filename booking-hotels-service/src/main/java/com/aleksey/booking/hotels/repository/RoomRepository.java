package com.aleksey.booking.hotels.repository;

import com.aleksey.booking.hotels.model.Room;
import jakarta.persistence.LockModeType;
import jakarta.persistence.QueryHint;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

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

    @Override
    Page<Room> findAll(Specification<Room> specification, Pageable pageable);
}