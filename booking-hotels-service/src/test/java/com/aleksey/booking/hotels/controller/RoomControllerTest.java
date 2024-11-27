package com.aleksey.booking.hotels.controller;

import com.aleksey.booking.hotels.api.request.RoomFilter;
import com.aleksey.booking.hotels.api.request.UpsertRoomRequest;
import com.aleksey.booking.hotels.api.response.RoomPaginationResponse;
import com.aleksey.booking.hotels.api.response.RoomResponse;
import com.aleksey.booking.hotels.service.RoomService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class RoomControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private RoomService roomService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void testGetRoomById() throws Exception {
        Long roomId = 1L;
        RoomResponse roomResponse = new RoomResponse(
                roomId,
                "Test Room",
                "Test Description",
                "Test Number",
                100,
                2,
                null,
                1L
        );

        when(roomService.getById(roomId)).thenReturn(roomResponse);

        mockMvc.perform(get("/api/room/{id}", roomId)
                        .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_ADMIN"), new SimpleGrantedAuthority("ROLE_USER"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(roomId))
                .andExpect(jsonPath("$.name").value("Test Room"));
    }

    @Test
    void testCreateRoom() throws Exception {
        UpsertRoomRequest request = new UpsertRoomRequest(
                "New Room",
                "Room Description",
                "Room Number",
                100,
                2,
                1L
        );

        RoomResponse roomResponse = new RoomResponse(
                1L,
                "New Room",
                "Room Description",
                "Room Number",
                100,
                2,
                null,
                1L
        );

        when(roomService.createRoom(any(UpsertRoomRequest.class))).thenReturn(roomResponse);

        mockMvc.perform(post("/api/room")
                        .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_ADMIN")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("New Room"));
    }

    @Test
    void testUpdateRoom() throws Exception {
        Long roomId = 1L;

        UpsertRoomRequest request = new UpsertRoomRequest(
                "Updated Room",
                "Room Description",
                "Room Number",
                100,
                2,
                1L
        );


        RoomResponse roomResponse = new RoomResponse(
                1L,
                "Updated Room",
                "Room Description",
                "Room Number",
                100,
                2,
                null,
                1L
        );
        when(roomService.updateRoom(eq(roomId), any(UpsertRoomRequest.class))).thenReturn(roomResponse);

        mockMvc.perform(put("/api/room/{id}", roomId)
                        .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_ADMIN")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(roomId))
                .andExpect(jsonPath("$.name").value("Updated Room"));
    }

    @Test
    void testDeleteRoom() throws Exception {
        Long roomId = 1L;

        doNothing().when(roomService).deleteRoom(roomId);

        mockMvc.perform(delete("/api/room/{id}", roomId)
                        .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_ADMIN"))))
                .andExpect(status().isNoContent());
    }

    @Test
    void testGetRoomPage() throws Exception {
        RoomFilter filter = new RoomFilter(
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null);
        RoomPaginationResponse paginationResponse = new RoomPaginationResponse(
                10L,
                null
        );

        when(roomService.filterBy(any(RoomFilter.class))).thenReturn(paginationResponse);

        mockMvc.perform(get("/api/room/roomPage")
                        .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_ADMIN"), new SimpleGrantedAuthority("ROLE_USER")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(filter)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.roomsCount").value(10L));
    }
}