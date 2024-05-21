package com.aleksey.booking.hotels.controller;

import com.aleksey.booking.hotels.api.request.UpsertHotelRequest;
import com.aleksey.booking.hotels.api.response.HotelListResponse;
import com.aleksey.booking.hotels.api.response.HotelResponse;
import com.aleksey.booking.hotels.service.HotelService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/hotel")
@RequiredArgsConstructor
public class HotelController {

    private final HotelService hotelService;

    @GetMapping
    public ResponseEntity<HotelListResponse> getAllHotels() {
        return ResponseEntity.ok(hotelService.findAllHotels());
    }

    @GetMapping("/{id}")
    public ResponseEntity<HotelResponse> getHotelById(@PathVariable Long id) {
        return ResponseEntity.ok(hotelService.findById(id));
    }

    @PostMapping
    public ResponseEntity<HotelResponse> createHotel(@RequestBody @Valid UpsertHotelRequest upsertHotelRequest) {
        return ResponseEntity.status(HttpStatus.CREATED).body(hotelService.createHotel(upsertHotelRequest));
    }

    @PutMapping("/{id}")
    public ResponseEntity<HotelResponse> updateHotel(@PathVariable Long id, @RequestBody @Valid UpsertHotelRequest upsertHotelRequest) {
        return ResponseEntity.ok(hotelService.updateHotel(id, upsertHotelRequest));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteHotel(@PathVariable Long id) {
        hotelService.deleteHotel(id);
        return ResponseEntity.noContent().build();
    }
}