package com.tga.booking.service;

import com.tga.booking.entity.BookingDAO;
import com.tga.booking.entity.SeatDAO;


import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;


import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest
class BookingServiceTest {

    @Autowired
    BookingService bookingService;
    static BookingDAO bookingDAO;
    static SeatDAO seatDAO;

    static {
        System.setProperty("spring.kafka.streams.state-dir", "/tmp/kafka-streams/" + UUID.randomUUID());
    }

    @BeforeAll
    static void beforeAll() {
        seatDAO = SeatDAO.builder().seatNumber(UUID.randomUUID().toString()).build();
        bookingDAO = new BookingDAO(UUID.randomUUID().toString(), List.of(seatDAO), 1.99);
        seatDAO.setOrder(bookingDAO);

    }

    @Test
    void save_persistOrder_persisted() {
        BookingDAO save = bookingService.save(bookingDAO);

        assertThat(save).isNotNull();

    }

    @Test
    void findByOrderNumber_isFound() {
        String orderNumberTest = UUID.randomUUID().toString();
        bookingDAO.setBookingNumber(orderNumberTest);
        bookingService.save(bookingDAO);

        Optional<BookingDAO> orderFound = bookingService.findByOrderNumber(orderNumberTest);
        assertThat(orderFound.get().getId()).isNotNull();
    }

    @Test
    void findByOrderNumber_notFound_optionalIsEmpty() {
        String orderNumberNotfound = "notfound";

        Optional<BookingDAO> orderResult = bookingService.findByOrderNumber(orderNumberNotfound);
        assertThat(orderResult.isPresent()).isFalse();
    }
}