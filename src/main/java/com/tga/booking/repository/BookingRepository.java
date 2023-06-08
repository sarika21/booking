package com.tga.booking.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.tga.booking.entity.BookingDAO;

import java.util.Optional;

@Repository
@Transactional
public interface BookingRepository extends CrudRepository<BookingDAO, Long> {

    @Transactional
    Optional<BookingDAO> findByBookingNumber(String orderNumber);

}
