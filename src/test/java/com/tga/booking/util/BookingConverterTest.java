package com.tga.booking.util;

import com.tga.booking.entity.BookingDAO;
import com.tga.booking.entity.SeatDAO;
import com.tga.booking.util.BookingConverter;
import com.tga.common.entity.Booking;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest
class BookingConverterTest {

    @Autowired
    BookingConverter bookingConverter;

    @Test
    void orderPojoToKafka_convertOrderDAOtoOrder_newOrder() {
        //create orderDAO
        SeatDAO seatDAO = SeatDAO.builder().seatNumber(UUID.randomUUID().toString()).id(1L).build();
        BookingDAO bookingDAO = new BookingDAO(UUID.randomUUID().toString(), List.of(seatDAO),1.99);
        seatDAO.setOrder(bookingDAO);

        //convert orderDAO to order from common package
        Booking order = bookingConverter.bookingDaoToKafka(bookingDAO);

        assertThat(order).isNotNull();
        assertThat(order).isInstanceOf(Booking.class);
    }

    @Test
    void listOrderPojoToKafka_convertListOfOrderDAOtoOrder_newListOfOrder() {
        List<BookingDAO> listOrderDAO = new ArrayList<>();
        SeatDAO seatDAO = SeatDAO.builder().seatNumber(UUID.randomUUID().toString()).id(1L).build();
        BookingDAO bookingDAO = new BookingDAO(UUID.randomUUID().toString(), List.of(seatDAO),1.99);
        seatDAO.setOrder(bookingDAO);
        listOrderDAO.add(bookingDAO);

        List<Booking> listOrder = bookingConverter.listOrderDaoToKafka(listOrderDAO);

        //size of order
        assertThat(listOrder.size()).isEqualTo(1);
        //size of items inside order
        assertThat(listOrder.get(0).getSeatInfo().size()).isEqualTo(1);
    }
}