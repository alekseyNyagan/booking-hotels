package com.aleksey.booking.hotels.controller;

import com.aleksey.booking.hotels.api.request.RoomFilter;
import com.aleksey.booking.hotels.api.request.UpsertRoomRequest;
import com.aleksey.booking.hotels.api.response.RoomPaginationResponse;
import com.aleksey.booking.hotels.api.response.RoomResponse;
import com.aleksey.booking.hotels.service.RoomService;
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
@RequestMapping("/api/room")
@RequiredArgsConstructor
@Tag(name = "Room API", description = "API for rooms")
public class RoomController {

    private final RoomService roomService;

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_USER')")
    @Operation(summary = "Get room by Id", description = "Returns room by Id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success", content = @Content(
                    schema = @Schema(implementation = RoomResponse.class)
            )),
            @ApiResponse(responseCode = "404", description = "Room not found")
    })
    public ResponseEntity<RoomResponse> getRoom(@Parameter(description = "Room Id", required = true, example = "1") @PathVariable Long id) {
        return ResponseEntity.ok(roomService.getById(id));
    }

    @PostMapping
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @Operation(summary = "Create room", description = "Creates room by provided data")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Room created successfully", content = @Content(
                    schema = @Schema(implementation = RoomResponse.class)
            )),
            @ApiResponse(responseCode = "400", description = "Bad Request")
    })
    public ResponseEntity<RoomResponse> createRoom(
            @Parameter(description = "Data for room creation", required = true) @RequestBody @Valid UpsertRoomRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(roomService.createRoom(request));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @Operation(summary = "Update room", description = "Updates room by provided data")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Room updated successfully", content = @Content(
                    schema = @Schema(implementation = RoomResponse.class)
            )),
            @ApiResponse(responseCode = "400", description = "Bad Request"),
            @ApiResponse(responseCode = "404", description = "Room not found")
    })
    public ResponseEntity<RoomResponse> updateRoom(
            @Parameter(description = "Room Id", required = true, example = "1") @PathVariable Long id,
            @Parameter(description = "Data for room update", required = true) @RequestBody @Valid UpsertRoomRequest request) {
        return ResponseEntity.ok(roomService.updateRoom(id, request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @Operation(summary = "Delete room", description = "Deletes room by provided Id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Room deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Room not found")
    })
    public ResponseEntity<Void> deleteRoom(@Parameter(description = "Room Id", required = true, example = "1") @PathVariable Long id) {
        roomService.deleteRoom(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/roomPage")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_USER')")
    @Operation(summary = "Get page of rooms", description = "Returns rooms by provided filter")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success", content = @Content(
                    schema = @Schema(implementation = RoomPaginationResponse.class)
            ))
    })
    public ResponseEntity<RoomPaginationResponse> roomPage(
            @Parameter(description = "Filter for rooms searching", required = true) @RequestBody RoomFilter filter) {
        return ResponseEntity.ok(roomService.filterBy(filter));
    }
}