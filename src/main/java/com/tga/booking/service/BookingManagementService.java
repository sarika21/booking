package com.tga.booking.service;

import com.tga.booking.entity.BookingDAO;
import com.tga.booking.exception.BookingException;
import com.tga.booking.producer.KafkaProducer;
import com.tga.booking.util.BookingConverter;
import com.tga.common.entity.Booking;
import com.tga.common.util.Status;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
public class BookingManagementService {

    private final BookingService bookingService;
    private final BookingConverter bookingConverter;
    private final KafkaProducer kafkaProducer;

    public BookingManagementService(BookingService bookingService, BookingConverter bookingConverter, KafkaProducer kafkaProducer) {
        this.bookingService = bookingService;
        this.bookingConverter = bookingConverter;
        this.kafkaProducer = kafkaProducer;
    }

    @Transactional
    public Booking processOrder(Booking stockOrder, Booking paymentOrder) {
            BookingDAO bookingDAO = bookingService.findByOrderNumber(stockOrder.getBookingNumber()).orElseThrow(() -> {
                log.error("something is wrong, order {} should be available but isn't, " +
                        "if here, put a breakpoint to check it",stockOrder.getBookingNumber());
                return new BookingException("Booking not found");
            });
            bookingDAO.setSeatStatus(stockOrder.getSeatStatus());
            bookingDAO.setSeatStatusReason(stockOrder.getSeatStatusReason());
            bookingDAO.setPaymentStatus(paymentOrder.getPaymentStatus());
            bookingDAO.setPaymentStatusReason(paymentOrder.getPaymentStatusReason());
            bookingService.save(bookingDAO);

            //where notifications can be triggered to user informing the final status of the order
            kafkaProducer.sendNotification(bookingDAO.getBookingNumber(), bookingConverter.bookingDaoToKafka(bookingDAO));

            if (bookingDAO.getSeatStatus().equalsIgnoreCase(Status.SUCCESS) &&
            		bookingDAO.getPaymentStatus().equalsIgnoreCase(Status.SUCCESS)) {
                log.debug("Booking succeeded {}", bookingDAO);
                return bookingConverter.bookingDaoToKafka(bookingDAO);
            }

            if (bookingDAO.getSeatStatus().equalsIgnoreCase(Status.FAILED) &&
            		bookingDAO.getPaymentStatus().equalsIgnoreCase(Status.FAILED)){
                log.debug("Booking failed {}", bookingDAO);
                return bookingConverter.bookingDaoToKafka(bookingDAO);
            }

            //if arrived here, at least one service failed
            if (bookingDAO.getSeatStatus().equalsIgnoreCase(Status.FAILED)) {
                //both failed
                if (bookingDAO.getPaymentStatus().equalsIgnoreCase(Status.FAILED)) {
                    log.debug("rollback Booking, both failed {}", bookingDAO);
                    bookingDAO.setSeatStatus(Status.ROLLBACK);
                    bookingDAO.setPaymentStatus(Status.ROLLBACK);
                    return bookingConverter.bookingDaoToKafka(bookingDAO);
                }
                //only seatStock failed
                log.debug("rollback Booking, only seatStock failed {}", bookingDAO);
                bookingDAO.setPaymentStatus(Status.ROLLBACK);
                return bookingConverter.bookingDaoToKafka(bookingDAO);
            }

            //if arrived here, only payment failed
            log.debug("rollback Booking, only payment failed {}", bookingDAO);
            bookingDAO.setSeatStatus(Status.ROLLBACK);
            return bookingConverter.bookingDaoToKafka(bookingDAO);
    }

}
