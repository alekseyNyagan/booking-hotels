package com.aleksey.booking.hotels.repository;

import com.aleksey.booking.hotels.api.request.HotelFilter;
import com.aleksey.booking.hotels.model.Hotel;
import org.springframework.data.jpa.domain.Specification;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

public class HotelSpecification implements Specification<Hotel> {

    private final HotelFilter filter;

    public HotelSpecification(HotelFilter filter) {
        this.filter = filter;
    }

    @Override
    public Predicate toPredicate(Root<Hotel> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
        Predicate predicate = criteriaBuilder.conjunction();

        if (filter.hotelName() != null) {
            predicate = criteriaBuilder.and(predicate, byHotelName(filter.hotelName()).toPredicate(root, query, criteriaBuilder));
        }

        if (filter.hotelId() != null) {
            predicate = criteriaBuilder.and(predicate, byHotelId(filter.hotelId()).toPredicate(root, query, criteriaBuilder));
        }

        if (filter.title() != null) {
            predicate = criteriaBuilder.and(predicate, byTitle(filter.title()).toPredicate(root, query, criteriaBuilder));
        }

        if (filter.city() != null) {
            predicate = criteriaBuilder.and(predicate, byCity(filter.city()).toPredicate(root, query, criteriaBuilder));
        }

        if (filter.address() != null) {
            predicate = criteriaBuilder.and(predicate, byAddress(filter.address()).toPredicate(root, query, criteriaBuilder));
        }

        if (filter.distance() != null) {
            predicate = criteriaBuilder.and(predicate, byDistance(filter.distance()).toPredicate(root, query, criteriaBuilder));
        }

        if (filter.rating() != null) {
            predicate = criteriaBuilder.and(predicate, byRating(filter.rating()).toPredicate(root, query, criteriaBuilder));
        }

        if (filter.marksCount() != null) {
            predicate = criteriaBuilder.and(predicate, byMarksCount(filter.marksCount()).toPredicate(root, query, criteriaBuilder));
        }

        return predicate;
    }

    public static Specification<Hotel> byHotelName(String hotelName) {
        return (root, query, criteriaBuilder) -> hotelName == null ? null : criteriaBuilder.equal(root.get("name"), hotelName);
    }

    public static Specification<Hotel> byHotelId(Long hotelId) {
        return (root, query, criteriaBuilder) -> hotelId == null ? null : criteriaBuilder.equal(root.get("id"), hotelId);
    }

    public static Specification<Hotel> byTitle(String title) {
        return (root, query, criteriaBuilder) -> title == null ? null : criteriaBuilder.equal(root.get("title"), title);
    }

    public static Specification<Hotel> byCity(String city) {
        return (root, query, criteriaBuilder) -> city == null ? null : criteriaBuilder.equal(root.get("city"), city);
    }

    public static Specification<Hotel> byAddress(String address) {
        return (root, query, criteriaBuilder) -> address == null ? null : criteriaBuilder.equal(root.get("address"), address);
    }

    public static Specification<Hotel> byDistance(Double distance) {
        return (root, query, criteriaBuilder) -> distance == null ? null : criteriaBuilder.equal(root.get("distance"), distance);
    }

    public static Specification<Hotel> byRating(Double rating) {
        return (root, query, criteriaBuilder) -> rating == null ? null : criteriaBuilder.equal(root.get("rating"), rating);
    }

    public static Specification<Hotel> byMarksCount(Integer marksCount) {
        return (root, query, criteriaBuilder) -> marksCount == null ? null : criteriaBuilder.equal(root.get("marksCount"), marksCount);
    }
}