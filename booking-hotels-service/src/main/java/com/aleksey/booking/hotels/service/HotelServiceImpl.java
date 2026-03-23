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
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
@RequiredArgsConstructor
public class HotelServiceImpl implements HotelService {

    private static final String HOTEL_NOT_FOUND_MSG = "Отель не найден!";

    private final HotelRepository hotelRepository;
    private final HotelMapper hotelMapper;

    @Override
    public HotelResponse findById(Long id) {
        return hotelMapper.toDto(hotelRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(HOTEL_NOT_FOUND_MSG)));
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
        Hotel hotel = hotelRepository.findById(rateRequest.hotelId())
                .orElseThrow(() -> new EntityNotFoundException(HOTEL_NOT_FOUND_MSG));

        Byte newMark = rateRequest.newMark();
        double rating = hotel.getRating();
        int marksCount = hotel.getMarksCount();

        if (marksCount == 0) {
            saveHotelWithNewRating(hotel, newMark.doubleValue());
            return;
        }

        BigDecimal totalRating = BigDecimal.valueOf(rating)
                .multiply(BigDecimal.valueOf(marksCount))
                .subtract(BigDecimal.valueOf(rating))
                .add(BigDecimal.valueOf(newMark));

        BigDecimal newRating = totalRating
                .divide(BigDecimal.valueOf(marksCount), 1, RoundingMode.HALF_DOWN);

        saveHotelWithNewRating(hotel, newRating.doubleValue(), ++marksCount);
    }

    @Override
    public HotelPaginationResponse filterBy(HotelFilter hotelFilter) {
        Page<Hotel> hotelsPage = hotelRepository.findAll(new HotelSpecification(hotelFilter),
                PageRequest.of(hotelFilter.pageNumber(), hotelFilter.pageSize()));
        return hotelMapper.hotelListToHotelPaginationResponse(hotelsPage.getTotalElements(), hotelsPage.getContent());
    }

    private void saveHotelWithNewRating(Hotel hotel, Double newRating, int marksCount) {
        hotel.setRating(newRating);
        hotel.setMarksCount(marksCount);
        hotelRepository.save(hotel);
    }

    private void saveHotelWithNewRating(Hotel hotel, Double newRating) {
        saveHotelWithNewRating(hotel, newRating, 1);
    }
}