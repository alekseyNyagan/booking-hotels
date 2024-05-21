package com.aleksey.booking.hotels.repository;

import com.aleksey.booking.hotels.model.Room;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RoomRepository extends JpaRepository<Room, Long> {

    @Override
    @NonNull
    @EntityGraph(attributePaths = {"unavailableDates"})
    Optional<Room> findById(@NonNull Long aLong);

    @EntityGraph(attributePaths = {"unavailableDates"})
    List<Room> findAllByIdIn(List<Long> ids);
}