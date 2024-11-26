package com.aleksey.booking.hotels.controller;

import com.aleksey.booking.hotels.api.request.UpsertBookingRequest;
import com.aleksey.booking.hotels.api.response.BookingPaginationResponse;
import com.aleksey.booking.hotels.api.response.BookingResponse;
import com.aleksey.booking.hotels.service.BookingService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
class BookingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private BookingService bookingService;

    /**
     * Given a user with the role of "USER" is authenticated<br>
     * And the user provides valid booking details including arrival date, departure date, and room IDs<br>
     * When the user submits the booking request<br>
     * Then the system should create a new booking<br>
     * And the response should indicate that the booking was created successfully<br>
     * And the response should include the booking details such as booking ID and dates
     */
    @Test
    void successfullyCreateNewBookingWithValidData() throws Exception {
        UpsertBookingRequest request = new UpsertBookingRequest("2023-10-01", "2023-10-10", List.of(1L, 2L));
        BookingResponse response = new BookingResponse(1L, LocalDate.parse("2023-10-01"), LocalDate.parse("2023-10-10"), List.of(), UUID.randomUUID());

        when(bookingService.createBooking(any(), any())).thenReturn(response);

        mockMvc.perform(post("/api/booking")
                        .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_USER")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.arrivalDate").value("2023-10-01"))
                .andExpect(jsonPath("$.departureDate").value("2023-10-10"));
    }

    /**
     * Given a user with the role of "USER" is authenticated<br>
     * And the user provides booking details with a missing arrival date<br>
     * When the user submits the booking request<br>
     * Then the system should reject the booking request<br>
     * And the response should indicate a "Bad request" error<br>
     * And the response should specify that the arrival date is required
     */
    @Test
    void attemptToCreateBookingWithMissingArrivalDate() throws Exception {
        UpsertBookingRequest request = new UpsertBookingRequest(null, "2023-10-10", List.of(1L, 2L));

        mockMvc.perform(post("/api/booking")
                        .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_USER")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorMessage").value("Дата прибытия должна быть указана!"));
    }

    /**
     * Given a user with the role of "ADMIN" is authenticated<br>
     * And the user requests a page of bookings with a specified page size and page number<br>
     * When the user submits the request for the booking page<br>
     * Then the system should return a list of bookings<br>
     * And the response should include the total number of bookings and the requested page of bookings
     */
    @Test
    void retrievePageOfBookingsAsAdmin() throws Exception {
        BookingPaginationResponse response = new BookingPaginationResponse(1L, List.of(new BookingResponse(1L, LocalDate.now(), LocalDate.now().plusDays(1), List.of(), UUID.randomUUID())));

        when(bookingService.getBookingPage(10, 1)).thenReturn(response);

        mockMvc.perform(get("/api/booking/bookingPage?pageSize=10&pageNumber=1")
                        .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_ADMIN"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.bookingsCount").value(1L));
    }

    /**
     * Given a user with the role of "USER" is authenticated<br>
     * And the user requests a page of bookings with a specified page size and page number<br>
     * When the user submits the request for the booking page<br>
     * Then the system should reject the request<br>
     * And the response should indicate that the user does not have the necessary permissions
     */
    @Test
    void attemptToRetrieveBookingsWithoutAdminPrivileges() throws Exception {
        mockMvc.perform(get("/api/booking/bookingPage?pageSize=10&pageNumber=1")
                        .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_USER"))))
                .andExpect(status().isForbidden());
    }

}