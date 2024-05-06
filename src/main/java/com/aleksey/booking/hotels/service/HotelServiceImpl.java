package com.aleksey.booking.hotels.service;


import com.aleksey.booking.hotels.api.request.UpsertHotelRequest;
import com.aleksey.booking.hotels.api.response.HotelListResponse;
import com.aleksey.booking.hotels.api.response.HotelResponse;

import com.aleksey.booking.hotels.mapper.HotelMapper;
import com.aleksey.booking.hotels.model.Hotel;
import com.aleksey.booking.hotels.repository.HotelRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
        hotel.orElseThrow(() -> new EntityNotFoundException("Отель не найден!"));
        return hotelMapper.toDto(hotel.get());
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
}
