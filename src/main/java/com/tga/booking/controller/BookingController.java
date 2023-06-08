package com.tga.booking.controller;

import com.tga.booking.entity.BookingDAO;
import com.tga.booking.entity.BookingPojoWrapper;
import com.tga.booking.producer.KafkaProducer;
import com.tga.booking.service.BookingService;
import com.tga.booking.util.BookingConverter;
import com.tga.booking.util.BookingGenerator;
import com.tga.common.entity.Booking;

import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("api/v1")
public class BookingController {

    private final KafkaTemplate<String, Booking> kafkaTemplate;
    private final BookingService bookingService;
    private final BookingConverter bookingConverter;
    private final BookingGenerator bookingGenerator;
    private final KafkaProducer kafkaProducer;

    public BookingController(KafkaTemplate<String, Booking> kafkaTemplate,
                           BookingService bookingService,
                           BookingConverter bookingConverter, BookingGenerator bookingGenerator, KafkaProducer kafkaProducer) {
        this.kafkaTemplate = kafkaTemplate;
        this.bookingService = bookingService;
        this.bookingConverter = bookingConverter;
        this.bookingGenerator = bookingGenerator;
        this.kafkaProducer = kafkaProducer;
    }

    @GetMapping("/booking")
    public ResponseEntity<?> sendOrders(@RequestParam(name = "o") Optional<Long> orders,
                                        @RequestParam(name = "s") Optional<Long> seats) {
        List<BookingDAO> bookDAOList = bookingGenerator.generateNewbookingDAOs(orders, seats);

        bookingService.saveAll(bookDAOList);

        List<Booking> orderList = bookingConverter.listOrderDaoToKafka(bookDAOList);
        orderList.forEach(order ->
                kafkaProducer.sendOrder(order.getBookingNumber(), order));

        return ResponseEntity.ok(new BookingPojoWrapper(orderList));
    }
}
