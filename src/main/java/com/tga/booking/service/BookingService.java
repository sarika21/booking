package com.tga.booking.service;

import com.tga.booking.entity.BookingDAO;
import com.tga.booking.repository.BookingRepository;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Slf4j
public class BookingService {

    private final BookingRepository bookingRepository;

    public BookingService(BookingRepository orderRepository) {
        this.bookingRepository = orderRepository;
    }

    public BookingDAO save(BookingDAO orderDAO){
        return bookingRepository.save(orderDAO);
    }

    public <S extends BookingDAO> Iterable<S> saveAll(Iterable<S> entities) {
        return bookingRepository.saveAll(entities);
    }

    public Optional<BookingDAO> findByOrderNumber(String orderNumber) {
        return bookingRepository.findByBookingNumber(orderNumber);
    }
}
