package com.aleksey.booking.hotels.controller;

import com.aleksey.booking.hotels.api.request.HotelFilter;
import com.aleksey.booking.hotels.api.request.UpsertHotelRequest;
import com.aleksey.booking.hotels.api.response.HotelListResponse;
import com.aleksey.booking.hotels.api.response.HotelPaginationResponse;
import com.aleksey.booking.hotels.api.response.HotelResponse;
import com.aleksey.booking.hotels.api.response.RateRequest;
import com.aleksey.booking.hotels.service.HotelService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/hotel")
@RequiredArgsConstructor
@Tag(name = "Hotel API", description = "API for Hotels")
public class HotelController {

    private final HotelService hotelService;

    @GetMapping
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_USER')")
    @Operation(summary = "Get all hotels", description = "Returns list of all hotels")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success", content = @Content(
                    schema = @Schema(implementation = HotelListResponse.class)
            ))
    })
    public ResponseEntity<HotelListResponse> getAllHotels() {
        return ResponseEntity.ok(hotelService.findAllHotels());
    }

    @GetMapping("{id}")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_USER')")
    @Operation(summary = "Get hotel by id", description = "Returns hotel by id")
    public ResponseEntity<HotelResponse> getHotelById(
            @Parameter(description = "Hotel id", required = true, example = "1") @PathVariable Long id) {
        return ResponseEntity.ok(hotelService.findById(id));
    }

    @PostMapping
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @Operation(summary = "Create hotel", description = "Creates new hotel with provided data")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Hotel created successfully", content = @Content(
                    schema = @Schema(implementation = HotelResponse.class)
            )),
            @ApiResponse(responseCode = "400", description = "Bad request")
    })
    public ResponseEntity<HotelResponse> createHotel(
            @Parameter(description = "Data to create hotel", required = true) @RequestBody @Valid UpsertHotelRequest upsertHotelRequest) {
        return ResponseEntity.status(HttpStatus.CREATED).body(hotelService.createHotel(upsertHotelRequest));
    }

    @PutMapping("{id}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @Operation(summary = "Update hotel", description = "Updates hotel with provided data")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Hotel updated successfully", content = @Content(
                    schema = @Schema(implementation = HotelResponse.class)
            )),
            @ApiResponse(responseCode = "400", description = "Bad request"),
            @ApiResponse(responseCode = "404", description = "Hotel not found")
    })
    public ResponseEntity<HotelResponse> updateHotel(
            @Parameter(description = "Hotel id", required = true, example = "1") @PathVariable Long id,
            @Parameter(description = "Data to update hotel", required = true) @RequestBody @Valid UpsertHotelRequest upsertHotelRequest) {
        return ResponseEntity.ok(hotelService.updateHotel(id, upsertHotelRequest));
    }

    @DeleteMapping("{id}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @Operation(summary = "Delete hotel", description = "Deletes hotel by id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Hotel deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Hotel not found")
    })
    public ResponseEntity<Void> deleteHotel(@Parameter(description = "Hotel id", required = true, example = "1") @PathVariable Long id) {
        hotelService.deleteHotel(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("rate")
    @Operation(summary = "Rate hotel", description = "Rate hotel by provided data")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Hotel rated successfully"),
            @ApiResponse(responseCode = "400", description = "Bad request"),
            @ApiResponse(responseCode = "404", description = "Hotel not found")
    })
    public ResponseEntity<Void> rate(
            @Parameter(description = "Data to rate hotel", required = true) @RequestBody @Valid RateRequest rateRequest) {
        hotelService.rateHotel(rateRequest);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("hotelPage")
    @Operation(summary = "Get hotel page", description = "Returns hotel page by provided filter")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success", content = @Content(
                    schema = @Schema(implementation = HotelPaginationResponse.class)
            ))
    })
    public ResponseEntity<HotelPaginationResponse> hotelPage(
            @Parameter(description = "Page size", example = "10") @RequestParam Integer pageSize,
            @Parameter(description = "Page number", example = "0") @RequestParam Integer pageNumber,
            @Parameter(description = "Hotel id") @RequestParam(required = false) Long hotelId,
            @Parameter(description = "Hotel name") @RequestParam(required = false) String hotelName,
            @Parameter(description = "Title") @RequestParam(required = false) String title,
            @Parameter(description = "City") @RequestParam(required = false) String city,
            @Parameter(description = "Address") @RequestParam(required = false) String address,
            @Parameter(description = "Max distance from center, km") @RequestParam(required = false) Double distance,
            @Parameter(description = "Min rating") @RequestParam(required = false) Double rating,
            @Parameter(description = "Min marks count") @RequestParam(required = false) Integer marksCount) {
        HotelFilter filter = new HotelFilter(pageSize, pageNumber, hotelId, hotelName, title, city, address, distance, rating, marksCount);
        return ResponseEntity.ok(hotelService.filterBy(filter));
    }
}