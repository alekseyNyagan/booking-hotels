package com.aleksey.booking.hotels.repository;

import com.aleksey.booking.hotels.model.Booking;
import com.aleksey.booking.hotels.model.Room;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@Testcontainers
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class BookingRepositoryTest {

    @Container
    private static final PostgreSQLContainer<?> postgresContainer = new PostgreSQLContainer<>("postgres:16.3")
            .withDatabaseName("testdb")
            .withUsername("testuser")
            .withPassword("testpassword");

    @DynamicPropertySource
    static void postgresqlProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgresContainer::getJdbcUrl);
        registry.add("spring.datasource.username", postgresContainer::getUsername);
        registry.add("spring.datasource.password", postgresContainer::getPassword);
    }

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private RoomRepository roomRepository;

    @BeforeEach
    void setUp() {
        bookingRepository.deleteAll();
        roomRepository.deleteAll();
    }

    @Test
    void testFindAll() {
        Room room1 = new Room();
        room1.setName("Room 1");
        roomRepository.save(room1);

        Room room2 = new Room();
        room2.setName("Room 2");
        roomRepository.save(room2);

        Booking booking1 = new Booking();
        booking1.setRooms(List.of(room1));
        bookingRepository.save(booking1);

        Booking booking2 = new Booking();
        booking2.setRooms(List.of(room2));
        bookingRepository.save(booking2);

        Pageable pageable = PageRequest.of(0, 10);
        Page<Booking> bookingsPage = bookingRepository.findAll(pageable);

        assertThat(bookingsPage.getContent()).hasSize(2);
        assertThat(bookingsPage.getContent().get(0).getRooms()).hasSize(1);
        assertThat(bookingsPage.getContent().get(1).getRooms()).hasSize(1);
    }
}