package com.tga.booking.util;

import com.tga.booking.entity.BookingDAO;
import com.tga.common.entity.Booking;
import com.tga.common.entity.SeatInfo;

import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class BookingConverter {

     public Booking bookingDaoToKafka(BookingDAO bookingDAO) {
             Booking orderKafka = new Booking(bookingDAO.getBookingNumber(),
                     bookingDAO.getSeats().stream().map(itemDAO ->
                             new SeatInfo(itemDAO.getSeatNumber(), itemDAO.getNoOfSeat())).collect(Collectors.toList()),
                     bookingDAO.getBookingPrice(), bookingDAO.getSeatStatus(), bookingDAO.getSeatStatusReason(), bookingDAO.getPaymentStatus(), bookingDAO.getPaymentStatusReason());
             return orderKafka;
    }

    public List<Booking> listOrderDaoToKafka(List<BookingDAO> orderDAOList){
         return orderDAOList.stream().map(this::bookingDaoToKafka).collect(Collectors.toList());

    }
}
