package com.tga.booking.util;

import com.tga.booking.entity.BookingDAO;
import com.tga.booking.entity.SeatDAO;
import com.tga.booking.exception.BookingException;
import com.tga.common.config.GlobalConfigs;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
public class BookingGenerator {

    private final GlobalConfigs globalConfigs;
    private final long maxSeatsAllowed;
    private long currentNumber=1;

    public BookingGenerator(GlobalConfigs globalConfigs,
                          @Value("${max.items.allowed}") long maxSeatsAllowed) {
        this.globalConfigs = globalConfigs;
        this.maxSeatsAllowed = maxSeatsAllowed;
    }

    /**
     * Generate new orderDAOs (to be persisted), quantity of item inside each order is determined by {@link GlobalConfigs}
     * @param reqBookings how many orders. Default = 5
     * @param reqSeats how many different items inside each order (for each different item the quantity is determined by {@link GlobalConfigs}. Default = 4
     * @return List of {@link BookingDAO} ready to be persisted
     */
    public List<BookingDAO> generateNewbookingDAOs(Optional<Long> reqBookings, Optional<Long> reqSeats) throws BookingException{
        long totalItemAmount = reqBookings.orElse(1L) * reqSeats.orElse(1L);
        if (totalItemAmount > maxSeatsAllowed){
            throw new BookingException("total no of req seat exceed the seat allowed. " +
                    "Total seat passed: "+totalItemAmount+ " max seat allowed : "+maxSeatsAllowed);
        }
        List<BookingDAO> orderDAOList = new ArrayList<>();

        for (int i = 0; i < reqBookings.orElse(5L); i++) {
            BookingDAO bookingDAO = new BookingDAO(UUID.randomUUID().toString(),
                    null,
                    Math.floor(Math.random() * (1000 - 1 + 1) + 1));

            bookingDAO.setSeats(generateNewItemDAOs(reqSeats.orElse(4L), bookingDAO));
            orderDAOList.add(bookingDAO);
        }

        return orderDAOList;
    }

    /**
     * generates list of {@link SeatDAO} to be included inside an {@link BookingDAO}
     * @param amountItems how many items to be generated
     * @param orderDAO the order to be linked with this list
     * @return
     */
    private List<SeatDAO> generateNewItemDAOs (Long amountItems, BookingDAO orderDAO) {
        List<SeatDAO> itemDaoList = new ArrayList<>();

        for (int j = 0; j < amountItems; j++) {
            itemDaoList.add(SeatDAO.builder()
                    .seatNumber(globalConfigs.itemPrefix()+currentNumber)
                    .noOfSeat(generateQuantity())
                    .order(orderDAO)
                    .build());
            currentNumber++;
        }

        return itemDaoList;
    }

    /**
     * used to determine the quantity of different items inside an order based on {@link GlobalConfigs} (e.g. all items should have success to reserve?).
     * all items in stock microservice have 10 units, therefore quantities above this will fail.
     * @return number representing the quantity of an item inside an order (e.g. an order of 16 microwaves)
     */
    private int generateQuantity() {
        if (globalConfigs.statusStock() == globalConfigs.SUCCESS) {
            return (int) Math.floor(Math.random() * (10 - 1 + 1) + 1);
        } else if (globalConfigs.statusStock() == globalConfigs.FAILURE) {
            return (int) Math.floor(Math.random() * (20 - 11 + 1) + 11);
        }
        //if config is invalid, return success,
        // could be configured differently (e.g. throw exception for no configuration found)
        return (int) Math.floor(Math.random() * (10 - 1 + 1) + 1);
    }
}
