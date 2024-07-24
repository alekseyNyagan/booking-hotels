package com.aleksey.booking.hotels.repository;

import com.aleksey.booking.hotels.api.request.RoomFilter;
import com.aleksey.booking.hotels.model.Room;
import com.aleksey.booking.hotels.model.UnavailableDate;
import com.aleksey.booking.hotels.utils.DateConverter;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Subquery;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;

public interface RoomSpecification {

    static Specification<Room> withFilter(RoomFilter filter) {
        return Specification.where(byRoomId(filter.roomId()))
                .and(byCostRange(filter.minCost(), filter.maxCost()))
                .and(byCountOfVisitors(filter.countOfVisitors()))
                .and(byAvailableDates(filter.arrivalDate(), filter.departureDate()))
                .and(byHotelId(filter.hotelId()));
    }

    static Specification<Room> byRoomId(Long roomId) {
        return (root, query, criteriaBuilder) -> {
            if (roomId == null) {
                return null;
            }

            return criteriaBuilder.equal(root.get("id"), roomId);
        };
    }

    static Specification<Room> byCostRange(Integer minCost, Integer maxCost) {
        return (root, query, criteriaBuilder) -> {
            if (minCost == null && maxCost == null) {
                return null;
            }
            if (maxCost == null) {
                return criteriaBuilder.greaterThanOrEqualTo(root.get("cost"), minCost);
            }
            if (minCost == null) {
                return criteriaBuilder.lessThanOrEqualTo(root.get("cost"), maxCost);
            }
            return criteriaBuilder.between(root.get("cost"), minCost, maxCost);
        };
    }

    static Specification<Room> byCountOfVisitors(Integer countOfVisitors) {
        return (root, query, criteriaBuilder) -> {
            if (countOfVisitors == null) {
                return null;
            }

            return criteriaBuilder.equal(root.get("max_count_of_people"), countOfVisitors);
        };
    }

    static Specification<Room> byAvailableDates(String arrivalDate, String departureDate) {
        return (root, query, criteriaBuilder) -> {
            if (arrivalDate == null || departureDate == null) {
                return null;
            }

            LocalDate arrivalLocalDate = DateConverter.fromStringDateToLocalDate(arrivalDate);
            LocalDate departureLocalDate = DateConverter.fromStringDateToLocalDate(departureDate);

            Subquery<Long> subquery = query.subquery(Long.class);
            Root<Room> subqueryRoomRoot = subquery.from(Room.class);
            Join<Room, UnavailableDate> unavailableDateJoin = subqueryRoomRoot.join("unavailableDates");
            subquery.select(subqueryRoomRoot.get("id"))
                    .where(criteriaBuilder.between(unavailableDateJoin.get("date"), arrivalLocalDate, departureLocalDate));

            return criteriaBuilder.not(root.get("id").in(subquery));
        };
    }

    static Specification<Room> byHotelId(Long hotelId) {
        return (root, query, criteriaBuilder) -> {
            if (hotelId == null) {
                return null;
            }

            return criteriaBuilder.equal(root.get("hotel").get("id"), hotelId);
        };
    }
}