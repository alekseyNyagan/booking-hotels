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
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class HotelServiceImplTest {

    @Mock
    private HotelRepository hotelRepository;

    @Mock
    private HotelMapper hotelMapper;

    @InjectMocks
    private HotelServiceImpl hotelService;

    /**
     * Given a hotel exists with a valid ID<br>
     * When a request is made to retrieve the hotel details using the valid ID<br>
     * Then the system should return the hotel information including name, title, city, and address<br>
     * And the response should indicate a successful retrieval of the hotel details
     */
    @Test
    void retrieveHotelDetailsByValidId() {
        Long hotelId = 1L;
        Hotel hotel = new Hotel();
        hotel.setId(hotelId);
        hotel.setName("Test Hotel");
        hotel.setTitle("A great place to stay");
        hotel.setCity("Test City");
        hotel.setAddress("123 Test St");

        HotelResponse hotelResponse = new HotelResponse(hotelId, "Test Hotel", "A great place to stay", "Test City", "123 Test St", null, null, null);

        when(hotelRepository.findById(hotelId)).thenReturn(Optional.of(hotel));
        when(hotelMapper.toDto(hotel)).thenReturn(hotelResponse);

        HotelResponse response = hotelService.findById(hotelId);

        assertEquals(hotelResponse, response);
        verify(hotelRepository).findById(hotelId);
    }

    /**
     * Given a hotel does not exist with the specified ID<br>
     * When a request is made to retrieve the hotel details using the invalid ID<br>
     * Then the system should throw an error indicating that the hotel was not found<br>
     * And the response should contain an appropriate error message
     */
    @Test
    void attemptToRetrieveHotelDetailsByInvalidId() {
        Long hotelId = 999L;

        when(hotelRepository.findById(hotelId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> hotelService.findById(hotelId));
        verify(hotelRepository).findById(hotelId);
    }

    /**
     * Given a request to create a new hotel with valid details<br>
     * When the hotel creation request is processed<br>
     * Then the system should save the new hotel information<br>
     * And the response should return the created hotel details including the assigned ID
     */
    @Test
    void createANewHotelWithValidDetails() {
        UpsertHotelRequest request = new UpsertHotelRequest("New Hotel", "Best place", "New City", "456 New St", 10F);
        Hotel hotel = new Hotel();
        hotel.setId(1L);
        hotel.setName("New Hotel");
        hotel.setTitle("Best place");
        hotel.setCity("New City");
        hotel.setAddress("456 New St");

        HotelResponse hotelResponse = new HotelResponse(1L, "New Hotel", "Best place", "New City", "456 New St", null, null, null);

        when(hotelMapper.toEntity(request)).thenReturn(hotel);
        when(hotelRepository.save(hotel)).thenReturn(hotel);
        when(hotelMapper.toDto(hotel)).thenReturn(hotelResponse);

        HotelResponse response = hotelService.createHotel(request);

        assertEquals(hotelResponse, response);
        verify(hotelRepository).save(hotel);
    }

    /**
     * Given a hotel exists with a valid ID<br>
     * And a request is made to update the hotel with new valid details<br>
     * When the update request is processed<br>
     * Then the system should update the hotel information<br>
     * And the response should return the updated hotel details
     */
    @Test
    void updateAnExistingHotelWithValidDetails() {
        Long hotelId = 1L;
        UpsertHotelRequest request = new UpsertHotelRequest("Updated Hotel", "Updated place", "Updated City", "789 Updated St", 15F);
        Hotel hotel = new Hotel();
        hotel.setId(hotelId);
        hotel.setName("Updated Hotel");
        hotel.setTitle("Updated place");
        hotel.setCity("Updated City");
        hotel.setAddress("789 Updated St");

        HotelResponse hotelResponse = new HotelResponse(hotelId, "Updated Hotel", "Updated place", "Updated City", "789 Updated St", null, null, null);

        when(hotelMapper.toEntity(hotelId, request)).thenReturn(hotel);
        when(hotelRepository.save(hotel)).thenReturn(hotel);
        when(hotelMapper.toDto(hotel)).thenReturn(hotelResponse);

        HotelResponse response = hotelService.updateHotel(hotelId, request);

        assertEquals(hotelResponse, response);
        verify(hotelRepository).save(hotel);
    }

    /**
     * Given a hotel exists with a valid ID<br>
     * When a request is made to delete the hotel using the valid ID<br>
     * Then the system should remove the hotel from the database<br>
     * And the response should confirm the successful deletion of the hotel
     */
    @Test
    void deleteHotelByValidId() {
        Long hotelId = 1L;

        hotelService.deleteHotel(hotelId);

        verify(hotelRepository).deleteById(hotelId);
    }

    /**
     * Given the hotel repository contains multiple hotel entries<br>
     * When a request is made to retrieve the list of all hotels<br>
     * Then the system should return a list of hotel responses<br>
     * And the list should contain the correct number of hotels<br>
     * And each hotel response should include valid details such as name, city, and rating
     */
    @Test
    void retrieveAllHotelsSuccessfully() {
        Long hotelId = 1L;
        Hotel hotel = new Hotel();
        hotel.setId(hotelId);
        hotel.setName("Test Hotel");
        hotel.setTitle("A great place to stay");
        hotel.setCity("Test City");
        hotel.setAddress("123 Test St");

        HotelResponse hotelResponse = new HotelResponse(
                1L,
                "Test Hotel",
                "A great place to stay",
                "Test City",
                "123 Test St",
                null,
                null,
                null);
        when(hotelRepository.findAll()).thenReturn(List.of(hotel));
        when(hotelMapper.hotelListToHotelListResponse(List.of(hotel))).thenReturn(new HotelListResponse(List.of(hotelResponse)));

        HotelListResponse result = hotelService.findAllHotels();

        assertEquals(1, result.hotels().size());
        assertEquals(hotelResponse, result.hotels().getFirst());
    }

    /**
     * Given the hotel repository is empty<br>
     * When a request is made to retrieve the list of all hotels<br>
     * Then the system should return an empty hotel response<br>
     * And the response should indicate that no hotels are available
     */
    @Test
    void handleEmptyHotelRepositoryGracefully() {
        when(hotelRepository.findAll()).thenReturn(Collections.emptyList());
        when(hotelMapper.hotelListToHotelListResponse(Collections.emptyList())).thenReturn(new HotelListResponse(Collections.emptyList()));

        HotelListResponse response = hotelService.findAllHotels();

        assertNotNull(response);
        assertTrue(response.hotels().isEmpty());

        assertEquals(0, response.hotels().size());
    }

    /**
     * Given a hotel exists with a unique identifier<br>
     * And the hotel has an existing rating and marks count<br>
     * When a user submits a rating request with a valid rating value<br>
     * Then the hotel rating should be updated based on the new rating<br>
     * And the marks count for the hotel should be incremented by one<br>
     * And the updated hotel information should be saved in the repository
     */
    @Test
    void successfullyRateHotelWithValidRating() {
        Long hotelId = 1L;
        RateRequest rateRequest = new RateRequest(hotelId, (byte) 4);
        Hotel hotel = new Hotel();
        hotel.setId(hotelId);
        hotel.setRating(3.0);
        hotel.setMarksCount(5);

        when(hotelRepository.findById(hotelId)).thenReturn(Optional.of(hotel));
        when(hotelRepository.save(hotel)).thenReturn(hotel);

        hotelService.rateHotel(rateRequest);

        assertEquals(3.2, hotel.getRating());
        assertEquals(6, hotel.getMarksCount());
        verify(hotelRepository).save(hotel);
    }

    /**
     * Given a hotel does not exist with the provided identifier<br>
     * When a user submits a rating request for the non-existent hotel<br>
     * Then the system should throw an EntityNotFoundException<br>
     * And the error message should indicate that the hotel was not found
     */
    @Test
    void attemptToRateNonExistentHotel() {
        Long hotelId = 999L;
        RateRequest rateRequest = new RateRequest(hotelId, (byte) 4);

        when(hotelRepository.findById(hotelId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> hotelService.rateHotel(rateRequest));
        verify(hotelRepository).findById(hotelId);
    }

    /**
     * Given a hotel exists with a unique identifier<br>
     * And the hotel has a current rating of zero and marks count of zero<br>
     * When a user submits a rating request with a valid rating value<br>
     * Then the hotel rating should be updated to the new rating value<br>
     * And the marks count for the hotel should be incremented by one<br>
     * And the updated hotel information should be saved in the repository
     */
    @Test
    void rateHotelWhenCurrentRatingIsZero() {
        Long hotelId = 1L;
        RateRequest rateRequest = new RateRequest(hotelId, (byte) 5);
        Hotel hotel = new Hotel();
        hotel.setId(hotelId);
        hotel.setRating(0.0);
        hotel.setMarksCount(0);

        when(hotelRepository.findById(hotelId)).thenReturn(Optional.of(hotel));
        when(hotelRepository.save(hotel)).thenReturn(hotel);

        hotelService.rateHotel(rateRequest);

        assertEquals(5.0, hotel.getRating());
        assertEquals(1, hotel.getMarksCount());
        verify(hotelRepository).save(hotel);
    }

    /**
     * Given a hotel exists with a unique identifier<br>
     * And the hotel has an existing rating and marks count<br>
     * When a user submits multiple rating requests with valid rating values<br>
     * Then the hotel rating should be updated correctly after each submission<br>
     * And the marks count for the hotel should reflect the total number of ratings submitted<br>
     * And the updated hotel information should be saved in the repository after each rating
     */
    @Test
    void rateHotelWithMultipleRatings() {
        Long hotelId = 1L;
        Hotel hotel = new Hotel();
        hotel.setId(hotelId);
        hotel.setRating(4.0);
        hotel.setMarksCount(2);

        when(hotelRepository.findById(hotelId)).thenReturn(Optional.of(hotel));
        when(hotelRepository.save(hotel)).thenReturn(hotel);

        RateRequest firstRateRequest = new RateRequest(hotelId, (byte) 5);
        hotelService.rateHotel(firstRateRequest);
        assertEquals(4.5, hotel.getRating());
        assertEquals(3, hotel.getMarksCount());
        verify(hotelRepository).save(hotel);

        RateRequest secondRateRequest = new RateRequest(hotelId, (byte) 3);
        hotelService.rateHotel(secondRateRequest);
        assertEquals(4.0, hotel.getRating());
        assertEquals(4, hotel.getMarksCount());
        verify(hotelRepository, times(2)).save(hotel);
    }

    /**
     * Given a hotel filter with a specified city and page number<br>
     * When the filter is applied to retrieve hotels<br>
     * Then the system should return a paginated list of hotels located in the specified city<br>
     * And the total number of hotels returned should match the expected count for that city
     */
    @Test
    void filterHotelsByCityAndPageNumber() {
        HotelFilter hotelFilter = new HotelFilter(10, 1, null, "Test Hotel", null, "Test City", null, null, null, null);
        List<Hotel> hotels = Arrays.asList(
                new Hotel(1L, "Test Hotel", "A great place to stay", "Test City", "123 Test St", 5F, 4.5, 10, new ArrayList<>()),
                new Hotel(2L, "Another Hotel", "Another great place", "Test City", "456 Another St", 10F, 4.0, 20, new ArrayList<>())
        );

        List<HotelResponse> hotelResponses = List.of(
                new HotelResponse(1L, "Test Hotel", "A great place to stay", "Test City", "123 Test St", 5.0, 4.5, 10),
                new HotelResponse(2L, "Another Hotel", "Another great place", "Test City", "456 Another St", 10.0, 4.0, 20)
        );

        doReturn(new PageImpl<>(hotels, PageRequest.of(1, 10), hotels.size()))
                .when(hotelRepository).findAll(any(HotelSpecification.class), any(PageRequest.class));

        doReturn(new HotelPaginationResponse(2L, hotelResponses))
                .when(hotelMapper).hotelListToHotelPaginationResponse(anyLong(), anyList());

        HotelPaginationResponse response = hotelService.filterBy(hotelFilter);

        assertNotNull(response);
        assertEquals(2, response.hotels().size());
        assertEquals("Test City", response.hotels().get(0).city());
        assertEquals("Test City", response.hotels().get(1).city());

        assertEquals(2, response.hotelsCount());
    }

    /**
     * Given a hotel filter with a specified minimum rating and marks count<br>
     * When the filter is applied to retrieve hotels<br>
     * Then the system should return a list of hotels that meet or exceed the specified rating<br>
     * And each hotel in the list should have a marks count greater than or equal to the specified count
     */
    @Test
    void filterHotelsByRatingAndMarksCount() {
        HotelFilter hotelFilter = new HotelFilter(10, 0, null, null, null, null, null, null, 4.0, 5);
        List<Hotel> hotels = Arrays.asList(
                new Hotel(1L, "Hotel A", "Title A", "City A", "Address A", 5F, 4.5, 10, new ArrayList<>()),
                new Hotel(2L, "Hotel B", "Title B", "City B", "Address B", 10F, 4.0, 5, new ArrayList<>())
        );

        List<HotelResponse> hotelResponses = List.of(
                new HotelResponse(1L, "Test Hotel", "A great place to stay", "Test City", "123 Test St", 5.0, 4.5, 10),
                new HotelResponse(2L, "Another Hotel", "Another great place", "Test City", "456 Another St", 10.0, 4.0, 20)
        );

        doReturn(new PageImpl<>(hotels, PageRequest.of(1, 10), hotels.size()))
                .when(hotelRepository).findAll(any(HotelSpecification.class), any(PageRequest.class));

        doReturn(new HotelPaginationResponse(2L, hotelResponses))
                .when(hotelMapper).hotelListToHotelPaginationResponse(anyLong(), anyList());

        HotelPaginationResponse response = hotelService.filterBy(hotelFilter);

        assertNotNull(response);
        assertEquals(2, response.hotels().size());
        for (HotelResponse hotelResponse : response.hotels()) {
            assertTrue(hotelResponse.rating() >= 4.0);
            assertTrue(hotelResponse.marksCount() >= 5);
        }
    }

    /**
     * Given a hotel filter with no criteria set<br>
     * When the filter is applied to retrieve hotels<br>
     * Then the system should return a complete list of all available hotels<br>
     * And the total number of hotels returned should reflect the total count in the database
     */
    @Test
    void filterHotelsWithNoCriteriaSpecified() {
        HotelFilter hotelFilter = new HotelFilter(10, 0, null, null, null, null, null, null, null, null);
        List<Hotel> hotels = Arrays.asList(
                new Hotel(1L, "Hotel A", "Title A", "City A", "Address A", 5F, 4.5, 10, new ArrayList<>()),
                new Hotel(2L, "Hotel B", "Title B", "City B", "Address B", 10F, 4.0, 20, new ArrayList<>())
        );

        List<HotelResponse> hotelResponses = List.of(
                new HotelResponse(1L, "Test Hotel", "A great place to stay", "Test City", "123 Test St", 5.0, 4.5, 10),
                new HotelResponse(2L, "Another Hotel", "Another great place", "Test City", "456 Another St", 10.0, 4.0, 20)
        );

        doReturn(new PageImpl<>(hotels, PageRequest.of(1, 10), hotels.size()))
                .when(hotelRepository).findAll(any(HotelSpecification.class), any(PageRequest.class));

        doReturn(new HotelPaginationResponse(2L, hotelResponses))
                .when(hotelMapper).hotelListToHotelPaginationResponse(anyLong(), anyList());

        HotelPaginationResponse response = hotelService.filterBy(hotelFilter);

        assertNotNull(response);
        assertEquals(2, response.hotels().size());

        assertEquals(2, response.hotelsCount());
    }

    /**
     * Given a hotel filter with a specified hotel name and address<br>
     * When the filter is applied to retrieve hotels<br>
     * Then the system should return a list of hotels that match the specified hotel name and address<br>
     * And the returned hotels should only include those that exactly match the criteria
     */
    @Test
    void filterHotelsByHotelNameAndAddress() {
        HotelFilter hotelFilter = new HotelFilter(10, 0, null, "Hotel A", null, "City A", "Address A", null, null, null);
        List<Hotel> hotels = List.of(
                new Hotel(1L, "Hotel A", "Title A", "City A", "Address A", 5F, 4.5, 10, new ArrayList<>())
        );

        List<HotelResponse> hotelResponses = List.of(
                new HotelResponse(1L, "Hotel A", "Title A", "City A", "Address A", 5.0, 4.5, 10)
        );

        doReturn(new PageImpl<>(hotels, PageRequest.of(1, 10), hotels.size()))
                .when(hotelRepository).findAll(any(HotelSpecification.class), any(PageRequest.class));

        doReturn(new HotelPaginationResponse(2L, hotelResponses))
                .when(hotelMapper).hotelListToHotelPaginationResponse(anyLong(), anyList());

        HotelPaginationResponse response = hotelService.filterBy(hotelFilter);

        assertNotNull(response);
        assertEquals(1, response.hotels().size());
        HotelResponse hotelResponse = response.hotels().getFirst();
        assertEquals("Hotel A", hotelResponse.name());
        assertEquals("Address A", hotelResponse.address());

        assertEquals("City A", hotelResponse.city());
    }

    /**
     * Given a hotel filter with a negative page number<br>
     * When the filter is applied to retrieve hotels<br>
     * Then the system should return an error indicating that the page number is invalid<br>
     * And no hotels should be returned in the response
     */
    @Test
    void filterHotelsWithInvalidPageNumber() {
        HotelFilter hotelFilter = new HotelFilter(10, -1, null, null, null, null, null, null, null, null);

        assertThrows(IllegalArgumentException.class, () -> hotelService.filterBy(hotelFilter));
    }
}