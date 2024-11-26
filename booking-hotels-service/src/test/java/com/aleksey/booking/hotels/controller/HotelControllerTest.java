package com.aleksey.booking.hotels.controller;

import com.aleksey.booking.hotels.api.request.HotelFilter;
import com.aleksey.booking.hotels.api.request.UpsertHotelRequest;
import com.aleksey.booking.hotels.api.response.HotelListResponse;
import com.aleksey.booking.hotels.api.response.HotelPaginationResponse;
import com.aleksey.booking.hotels.api.response.HotelResponse;
import com.aleksey.booking.hotels.api.response.RateRequest;
import com.aleksey.booking.hotels.service.HotelService;
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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class HotelControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private HotelService hotelService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void testGetAllHotels() throws Exception {
        HotelListResponse hotelListResponse = mock(HotelListResponse.class);
        when(hotelService.findAllHotels()).thenReturn(hotelListResponse);

        mockMvc.perform(get("/api/hotel")
                        .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_ADMIN"), new SimpleGrantedAuthority("ROLE_USER"))))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));

        verify(hotelService, times(1)).findAllHotels();
    }

    @Test
    void testGetHotelById() throws Exception {
        Long hotelId = 1L;
        HotelResponse hotelResponse = mock(HotelResponse.class);
        when(hotelService.findById(hotelId)).thenReturn(hotelResponse);

        mockMvc.perform(get("/api/hotel/{id}", hotelId)
                        .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_ADMIN"), new SimpleGrantedAuthority("ROLE_USER"))))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));

        verify(hotelService, times(1)).findById(hotelId);
    }

    @Test
    void testCreateHotel() throws Exception {
        UpsertHotelRequest upsertHotelRequest = new UpsertHotelRequest(
                "name",
                "title",
                "city",
                "address",
                1.0F);
        HotelResponse hotelResponse = mock(HotelResponse.class);
        when(hotelService.createHotel(any(UpsertHotelRequest.class))).thenReturn(hotelResponse);

        mockMvc.perform(post("/api/hotel")
                        .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_ADMIN")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(upsertHotelRequest)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));

        verify(hotelService, times(1)).createHotel(any(UpsertHotelRequest.class));
    }

    @Test
    void testUpdateHotel() throws Exception {
        Long hotelId = 1L;
        UpsertHotelRequest upsertHotelRequest = new UpsertHotelRequest(
                "name",
                "title",
                "city",
                "address",
                1.0F);
        HotelResponse hotelResponse = mock(HotelResponse.class);
        when(hotelService.updateHotel(eq(hotelId), any(UpsertHotelRequest.class))).thenReturn(hotelResponse);

        mockMvc.perform(put("/api/hotel/{id}", hotelId)
                        .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_ADMIN")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(upsertHotelRequest)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));

        verify(hotelService, times(1)).updateHotel(eq(hotelId), any(UpsertHotelRequest.class));
    }

    @Test
    void testDeleteHotel() throws Exception {
        Long hotelId = 1L;

        mockMvc.perform(delete("/api/hotel/{id}", hotelId)
                        .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_ADMIN"))))
                .andExpect(status().isNoContent());

        verify(hotelService, times(1)).deleteHotel(hotelId);
    }

    @Test
    void testRateHotel() throws Exception {
        RateRequest rateRequest = new RateRequest(1L, (byte) 5);

        mockMvc.perform(put("/api/hotel/rate")
                        .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_ADMIN"), new SimpleGrantedAuthority("ROLE_USER")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(rateRequest)))
                .andExpect(status().isNoContent());

        verify(hotelService, times(1)).rateHotel(any(RateRequest.class));
    }

    @Test
    void testHotelPage() throws Exception {
        HotelFilter hotelFilter = mock(HotelFilter.class);
        HotelPaginationResponse hotelPaginationResponse = mock(HotelPaginationResponse.class);
        when(hotelService.filterBy(any(HotelFilter.class))).thenReturn(hotelPaginationResponse);

        mockMvc.perform(get("/api/hotel/hotelPage")
                        .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_ADMIN"), new SimpleGrantedAuthority("ROLE_USER")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(hotelFilter)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));

        verify(hotelService, times(1)).filterBy(any(HotelFilter.class));
    }
}