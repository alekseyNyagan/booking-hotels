package com.aleksey.booking.hotels.repository;

import com.aleksey.booking.hotels.api.request.RoomFilter;
import com.aleksey.booking.hotels.model.Room;
import com.aleksey.booking.hotels.model.UnavailableDate;
import com.aleksey.booking.hotels.utils.DateConverter;
import jakarta.persistence.criteria.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.lang.Nullable;

import java.time.LocalDate;

public class RoomSpecification implements Specification<Room> {

    private final RoomFilter filter;

    public RoomSpecification(RoomFilter filter) {
        this.filter = filter;
    }

    @Override
    public Predicate toPredicate(Root<Room> root, @Nullable CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
        Predicate predicate = criteriaBuilder.conjunction();

        if (filter.roomId() != null) {
            predicate = criteriaBuilder.and(predicate, byRoomId(filter.roomId()).toPredicate(root, query, criteriaBuilder));
        }

        if (filter.minCost() != null && filter.maxCost() != null) {
            predicate = criteriaBuilder.and(predicate, byCostRange(filter.minCost(), filter.maxCost()).toPredicate(root, query, criteriaBuilder) );
        } else if (filter.minCost() == null && filter.maxCost() != null) {
            predicate = criteriaBuilder.and(predicate, byMaxCostLesserThanOrEqualTo(filter.maxCost()).toPredicate(root, query, criteriaBuilder));
        } else if (filter.minCost() != null) {
            predicate = criteriaBuilder.and(predicate, byMinCostGraterThanOrEqualTo(filter.minCost()).toPredicate(root, query, criteriaBuilder));
        }

        if (filter.countOfVisitors() != null) {
            predicate = criteriaBuilder.and(predicate, byCountOfVisitors(filter.countOfVisitors()).toPredicate(root, query, criteriaBuilder));
        }

        if (filter.arrivalDate() != null && filter.departureDate() != null) {
            predicate = criteriaBuilder.and(predicate, byAvailableDates(filter.arrivalDate(), filter.departureDate()).toPredicate(root, query, criteriaBuilder));
        }

        if (filter.hotelId() != null) {
            predicate = criteriaBuilder.and(predicate, byHotelId(filter.hotelId()).toPredicate(root, query, criteriaBuilder));
        }

        return predicate;
    }

    public static Specification<Room> byRoomId(Long roomId) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("id"), roomId);
    }

    public static Specification<Room> byCostRange(Integer minCost, Integer maxCost) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.between(root.get("cost"), minCost, maxCost);
    }

    public static Specification<Room> byMinCostGraterThanOrEqualTo(Integer minCost) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.greaterThanOrEqualTo(root.get("cost"), minCost);
    }

    public static Specification<Room> byMaxCostLesserThanOrEqualTo(Integer maxCost) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.lessThanOrEqualTo(root.get("cost"), maxCost);
    }

    public static Specification<Room> byCountOfVisitors(Integer countOfVisitors) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("max_count_of_people"), countOfVisitors);
    }

    public static Specification<Room> byAvailableDates(String arrivalDate, String departureDate) {
        return (root, query, criteriaBuilder) -> {
            query.distinct(true);

            LocalDate arrival = DateConverter.fromStringDateToLocalDate(arrivalDate);
            LocalDate departure = DateConverter.fromStringDateToLocalDate(departureDate);

            Join<Room, UnavailableDate> join = root.join("unavailableDates", JoinType.LEFT);

            Predicate dateInRange = criteriaBuilder.between(join.get("date"), arrival, departure);
            Predicate noConflicts = criteriaBuilder.isNull(join.get("date"));
            Predicate notBookedInRange = criteriaBuilder.not(dateInRange);

            return criteriaBuilder.or(noConflicts, notBookedInRange);
        };
    }

    public static Specification<Room> byHotelId(Long hotelId) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("hotel").get("id"), hotelId);
    }
}