package com.aleksey.booking.hotels.service;


import com.aleksey.booking.hotels.api.request.HotelFilter;
import com.aleksey.booking.hotels.api.request.UpsertHotelRequest;
import com.aleksey.booking.hotels.api.response.HotelListResponse;
import com.aleksey.booking.hotels.api.response.HotelPaginationResponse;
import com.aleksey.booking.hotels.api.response.HotelResponse;
import com.aleksey.booking.hotels.api.response.RateRequest;
import com.aleksey.booking.hotels.mapper.HotelMapper;
import com.aleksey.booking.hotels.model.Hotel;
import com.aleksey.booking.hotels.repository.HotelRepository;
import com.aleksey.booking.hotels.repository.HotelSpecification;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Optional;

@Service
public class HotelServiceImpl implements HotelService {

    private final HotelRepository hotelRepository;

    private final HotelMapper hotelMapper;

    @Autowired
    public HotelServiceImpl(HotelRepository hotelRepository, HotelMapper hotelMapper) {
        this.hotelRepository = hotelRepository;
        this.hotelMapper = hotelMapper;
    }

    @Override
    public HotelResponse findById(Long id) {
        Optional<Hotel> hotel = hotelRepository.findById(id);
        return hotelMapper.toDto(hotel.orElseThrow(() -> new EntityNotFoundException("Отель не найден!")));
    }

    @Override
    public HotelResponse createHotel(UpsertHotelRequest upsertHotelRequest) {
        Hotel hotel = hotelMapper.toEntity(upsertHotelRequest);
        hotelRepository.save(hotel);
        return hotelMapper.toDto(hotel);
    }

    @Override
    public HotelResponse updateHotel(Long id, UpsertHotelRequest upsertHotelRequest) {
        Hotel hotel = hotelMapper.toEntity(id, upsertHotelRequest);
        hotelRepository.save(hotel);
        return hotelMapper.toDto(hotel);
    }

    @Override
    public void deleteHotel(Long id) {
        hotelRepository.deleteById(id);
    }

    @Override
    public HotelListResponse findAllHotels() {
        return hotelMapper.hotelListToHotelListResponse(hotelRepository.findAll());
    }

    @Override
    public void rateHotel(RateRequest rateRequest) {
        Hotel hotel = hotelRepository.findById(rateRequest.hotelId()).orElseThrow(() ->
                new EntityNotFoundException("Отель не найден!"));

        double rating = hotel.getRating();
        int marksCount = hotel.getMarksCount();

        BigDecimal totalRating = BigDecimal.valueOf(rating)
                .multiply(BigDecimal.valueOf(marksCount))
                .subtract(BigDecimal.valueOf(rating))
                .add(BigDecimal.valueOf(rateRequest.newMark()));

        BigDecimal newRating = totalRating
                .divide(BigDecimal.valueOf(marksCount), 1, RoundingMode.HALF_DOWN);

        hotel.setRating(newRating.doubleValue());
        hotel.setMarksCount(++marksCount);

        hotelRepository.save(hotel);
    }

    @Override
    public HotelPaginationResponse filterBy(HotelFilter hotelFilter) {
        Page<Hotel> hotelsPage = hotelRepository.findAll(new HotelSpecification(hotelFilter),
                PageRequest.of(hotelFilter.pageNumber(), hotelFilter.pageSize()));
        return hotelMapper.hotelListToHotelPaginationResponse(hotelsPage.getTotalElements(), hotelsPage.getContent());
    }
}