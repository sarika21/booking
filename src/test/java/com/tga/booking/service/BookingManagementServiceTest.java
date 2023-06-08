package com.tga.booking.service;

import com.tga.booking.entity.BookingDAO;
import com.tga.booking.producer.KafkaProducer;
import com.tga.booking.service.BookingManagementService;
import com.tga.booking.service.BookingService;
import com.tga.booking.util.BookingConverter;
import com.tga.booking.util.BookingGenerator;
import com.tga.common.entity.Booking;
import com.tga.common.util.Status;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest
class BookingManagementServiceTest {

    @MockBean
    BookingService bookingService;
    @MockBean
    KafkaProducer kafkaProducer;
    @Autowired
    BookingConverter bookingConverter;
    @Autowired
    BookingGenerator bookingGenerator;
    @Autowired
    BookingManagementService orderManagementService;


    @Test
    void processOrder_allSuccess_true() {
        BookingDAO bookingDAO = bookingGenerator.generateNewbookingDAOs(Optional.of(1L), Optional.of(1L)).get(0);
        bookingDAO.setSeatStatus(Status.SUCCESS);
        bookingDAO.setPaymentStatus(Status.SUCCESS);
        Booking order = bookingConverter.bookingDaoToKafka(bookingDAO);

        Mockito.when(bookingService.save(bookingDAO)).thenReturn(bookingDAO);
        Mockito.when(bookingService.findByOrderNumber(bookingDAO.getBookingNumber())).thenReturn(Optional.of(bookingDAO));
        Booking orderResult = orderManagementService.processOrder(order, order);

        assertThat(orderResult.getSeatStatus()).isEqualTo(Status.SUCCESS);
        assertThat(orderResult.getPaymentStatus()).isEqualTo(Status.SUCCESS);
    }

    @Test
    void processOrder_allFailed_allFailed() {
        BookingDAO bookingDAO = bookingGenerator.generateNewbookingDAOs(Optional.of(1L), Optional.of(1L)).get(0);
        bookingDAO.setSeatStatus(Status.FAILED);
        bookingDAO.setPaymentStatus(Status.FAILED);
        Booking order = bookingConverter.bookingDaoToKafka(bookingDAO);

        Mockito.when(bookingService.save(bookingDAO)).thenReturn(bookingDAO);
        Mockito.when(bookingService.findByOrderNumber(bookingDAO.getBookingNumber())).thenReturn(Optional.of(bookingDAO));
        Booking orderResult = orderManagementService.processOrder(order, order);

        assertThat(orderResult.getSeatStatus()).isEqualTo(Status.FAILED);
        assertThat(orderResult.getPaymentStatus()).isEqualTo(Status.FAILED);
    }

    @Test
    void processOrder_stockFailed_paymentRollback() {
        BookingDAO bookingDAO = bookingGenerator.generateNewbookingDAOs(Optional.of(1L), Optional.of(1L)).get(0);
        bookingDAO.setSeatStatus(Status.FAILED);
        bookingDAO.setPaymentStatus(Status.SUCCESS);
        Booking order = bookingConverter.bookingDaoToKafka(bookingDAO);

        Mockito.when(bookingService.save(bookingDAO)).thenReturn(bookingDAO);
        Mockito.when(bookingService.findByOrderNumber(bookingDAO.getBookingNumber())).thenReturn(Optional.of(bookingDAO));
        Booking orderResult = orderManagementService.processOrder(order, order);

        assertThat(orderResult.getSeatStatus()).isEqualTo(Status.FAILED);
        assertThat(orderResult.getPaymentStatus()).isEqualTo(Status.ROLLBACK);
    }

    @Test
    void processOrder_paymentFailed_stockRollback() {
        BookingDAO bookingDAO = bookingGenerator.generateNewbookingDAOs(Optional.of(1L), Optional.of(1L)).get(0);
        bookingDAO.setSeatStatus(Status.SUCCESS);
        bookingDAO.setPaymentStatus(Status.FAILED);
        Booking order = bookingConverter.bookingDaoToKafka(bookingDAO);

        Mockito.when(bookingService.save(bookingDAO)).thenReturn(bookingDAO);
        Mockito.when(bookingService.findByOrderNumber(bookingDAO.getBookingNumber())).thenReturn(Optional.of(bookingDAO));
        Booking orderResult = orderManagementService.processOrder(order, order);

        assertThat(orderResult.getSeatStatus()).isEqualTo(Status.ROLLBACK);
        assertThat(orderResult.getPaymentStatus()).isEqualTo(Status.FAILED);
    }
}