package com.tga.booking.util;

import com.tga.booking.entity.BookingDAO;
import com.tga.booking.entity.SeatDAO;
import com.tga.booking.exception.BookingException;
import com.tga.booking.util.BookingGenerator;
import com.tga.common.config.GlobalConfigs;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.annotation.DirtiesContext;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;


@SpringBootTest
class BookingGeneratorTest {

    @Autowired
    BookingGenerator bookingGenerator;
    @MockBean
    GlobalConfigs globalConfigs;

    @BeforeEach
    void setUp() {
        Mockito.when(globalConfigs.itemPrefix()).thenReturn("seatnumber");
    }
    static {
        System.setProperty("spring.kafka.streams.state-dir", "/tmp/kafka-streams/" + UUID.randomUUID());
    }

    @Test
    void generateNewOrderDAOs_allOrdersGenerated_true() {
        var order = Optional.of(3L);
        var item = Optional.of(5L);

        List<BookingDAO> orderList = bookingGenerator.generateNewbookingDAOs(order, item);
        assertThat(orderList.size()).isEqualTo(3);
    }

    @Test
    void generateNewOrderDAOs_globalConfigFailure_quantityAbove10() {
        Mockito.when(globalConfigs.statusStock()).thenReturn(-1);
        var order = Optional.of(3L);
        var item = Optional.of(5L);

        List<BookingDAO> orderList = bookingGenerator.generateNewbookingDAOs(order, item);
        List<Long> itemQuantities = orderList.stream()
                .flatMap(orderDAO -> orderDAO.getSeats().stream())
                .map(SeatDAO::getNoOfSeat)
                .collect(Collectors.toList());
        itemQuantities.forEach(
                itemQuantity -> assertThat(itemQuantity).isGreaterThan(10));
    }

    @Test
    void generateNewOrderDAOs_itemNumbersAreInSequence_true() {
        var order = Optional.of(5L);
        var item = Optional.of(3L);
        long lastItemNumber = (order.get() * item.get());

        List<BookingDAO> orderList = bookingGenerator.generateNewbookingDAOs(order, item);

        assertThat(orderList.get(4).getSeats().get(2).getSeatNumber()).isEqualTo(globalConfigs.itemPrefix()+lastItemNumber);
    }

    @Test
    void generateNewOrderDAOs_maxAmountInvalid_throwsOrderException() {
        var order = Optional.of(2L);
        var item = Optional.of(501L);

        Assertions.assertThrows(BookingException.class,() ->
                bookingGenerator.generateNewbookingDAOs(order, item));
    }
}