package com.aleksey.booking.hotels.repository;

import com.aleksey.booking.hotels.api.request.HotelFilter;
import com.aleksey.booking.hotels.model.Hotel;
import org.springframework.data.jpa.domain.Specification;

public interface HotelSpecification {

    static Specification<Hotel> withFilter(HotelFilter filter) {
        return Specification.where(byHotelName(filter.hotelName()))
                .and(byHotelId(filter.hotelId()))
                .and(byTitle(filter.title()))
                .and(byCity(filter.city()))
                .and((byAddress(filter.address())))
                .and(byDistance(filter.distance()))
                .and(byRating(filter.rating()))
                .and(byMarksCount(filter.marksCount()));
    }

    static Specification<Hotel> byHotelName(String hotelName) {
        return ((root, query, criteriaBuilder) -> {
            if (hotelName == null) {
                return null;
            }

            return criteriaBuilder.equal(root.get("name"), hotelName);
        });
    }

    static Specification<Hotel> byHotelId(Long hotelId) {
        return ((root, query, criteriaBuilder) -> {
            if (hotelId == null) {
                return null;
            }

            return criteriaBuilder.equal(root.get("id"), hotelId);
        });
    }

    static Specification<Hotel> byTitle(String title) {
        return ((root, query, criteriaBuilder) -> {
            if (title == null) {
                return null;
            }

            return criteriaBuilder.equal(root.get("title"), title);
        });
    }

    static Specification<Hotel> byCity(String city) {
        return ((root, query, criteriaBuilder) -> {
            if (city == null) {
                return null;
            }

            return criteriaBuilder.equal(root.get("city"), city);
        });
    }

    static Specification<Hotel> byAddress(String address) {
        return ((root, query, criteriaBuilder) -> {
            if (address == null) {
                return null;
            }

            return criteriaBuilder.equal(root.get("address"), address);
        });
    }

    static Specification<Hotel> byDistance(Double distance) {
        return ((root, query, criteriaBuilder) -> {
            if (distance == null) {
                return null;
            }
            return criteriaBuilder.equal(root.get("distance"), distance);
        });
    }

    static Specification<Hotel> byRating(Double rating) {
        return ((root, query, criteriaBuilder) -> {
            if (rating == null) {
                return null;
            }
            return criteriaBuilder.equal(root.get("rating"), rating);
        });
    }

    static Specification<Hotel> byMarksCount(Integer marksCount) {
        return ((root, query, criteriaBuilder) -> {
            if (marksCount == null) {
                return null;
            }

            return criteriaBuilder.equal(root.get("marksCount"), marksCount);
        });
    }
}