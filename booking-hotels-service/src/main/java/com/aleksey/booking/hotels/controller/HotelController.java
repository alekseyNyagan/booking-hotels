package com.aleksey.booking.hotels.controller;

import com.aleksey.booking.hotels.api.request.HotelFilter;
import com.aleksey.booking.hotels.api.request.UpsertHotelRequest;
import com.aleksey.booking.hotels.api.response.HotelListResponse;
import com.aleksey.booking.hotels.api.response.HotelPaginationResponse;
import com.aleksey.booking.hotels.api.response.HotelResponse;
import com.aleksey.booking.hotels.api.response.RateRequest;
import com.aleksey.booking.hotels.service.HotelService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/hotel")
@RequiredArgsConstructor
public class HotelController {

    private final HotelService hotelService;

    @GetMapping
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_USER')")
    public ResponseEntity<HotelListResponse> getAllHotels() {
        return ResponseEntity.ok(hotelService.findAllHotels());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_USER')")
    public ResponseEntity<HotelResponse> getHotelById(@PathVariable Long id) {
        return ResponseEntity.ok(hotelService.findById(id));
    }

    @PostMapping
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<HotelResponse> createHotel(@RequestBody @Valid UpsertHotelRequest upsertHotelRequest) {
        return ResponseEntity.status(HttpStatus.CREATED).body(hotelService.createHotel(upsertHotelRequest));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<HotelResponse> updateHotel(@PathVariable Long id, @RequestBody @Valid UpsertHotelRequest upsertHotelRequest) {
        return ResponseEntity.ok(hotelService.updateHotel(id, upsertHotelRequest));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<Void> deleteHotel(@PathVariable Long id) {
        hotelService.deleteHotel(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/rate")
    public ResponseEntity<Void> rate(@RequestBody @Valid RateRequest rateRequest) {
        hotelService.rateHotel(rateRequest);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/hotelPage")
    public ResponseEntity<HotelPaginationResponse> hotelPage(@RequestBody HotelFilter filter) {
        return ResponseEntity.ok(hotelService.filterBy(filter));
    }
}