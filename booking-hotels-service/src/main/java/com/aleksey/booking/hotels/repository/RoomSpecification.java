package com.aleksey.booking.hotels.repository;

import com.aleksey.booking.hotels.api.request.RoomFilter;
import com.aleksey.booking.hotels.model.Room;
import com.aleksey.booking.hotels.model.UnavailableDate;
import com.aleksey.booking.hotels.utils.DateConverter;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Subquery;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.jpa.domain.Specification;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import java.time.LocalDate;

public class RoomSpecification implements Specification<Room> {

    private final RoomFilter filter;

    public RoomSpecification(RoomFilter filter) {
        this.filter = filter;
    }

    @Override
    public Predicate toPredicate(@NotNull Root<Room> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
        Predicate predicate = criteriaBuilder.conjunction();

        if (filter.roomId() != null) {
            predicate = criteriaBuilder.and(predicate, byRoomId(filter.roomId()).toPredicate(root, query, criteriaBuilder));
        }

        if (filter.minCost() != null || filter.maxCost() != null) {
            predicate = criteriaBuilder.and(predicate, byCostRange(filter.minCost(), filter.maxCost()).toPredicate(root, query, criteriaBuilder));
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
        return (root, query, criteriaBuilder) -> roomId == null ? null : criteriaBuilder.equal(root.get("id"), roomId);
    }

    public static Specification<Room> byCostRange(Integer minCost, Integer maxCost) {
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

    public static Specification<Room> byCountOfVisitors(Integer countOfVisitors) {
        return (root, query, criteriaBuilder) -> countOfVisitors == null ? null : criteriaBuilder.equal(root.get("max_count_of_people"), countOfVisitors);
    }

    public static Specification<Room> byAvailableDates(String arrivalDate, String departureDate) {
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

    public static Specification<Room> byHotelId(Long hotelId) {
        return (root, query, criteriaBuilder) -> hotelId == null ? null : criteriaBuilder.equal(root.get("hotel").get("id"), hotelId);
    }
}