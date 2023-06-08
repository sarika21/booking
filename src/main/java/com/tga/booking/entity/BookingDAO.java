package com.tga.booking.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.tga.common.util.Status;

import lombok.ToString;

import javax.persistence.*;
import java.util.List;

@Entity
@ToString
public class BookingDAO {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;
    private String bookingNumber;
    @OneToMany(
            mappedBy = "order",
            cascade = CascadeType.ALL)
    @JsonManagedReference
    private List<SeatDAO> seats;
    private double bookingPrice;
    @Column(nullable = false)
    private String seatStatus = Status.NEW;
    private String seatStatusReason;
    private String paymentStatus = Status.NEW;
    private String paymentStatusReason;

    public BookingDAO() {
    }

    public BookingDAO(String orderNumber, List<SeatDAO> items, double orderPrice) {
        this.bookingNumber = orderNumber;
        this.seats = items;
        this.bookingPrice = orderPrice;
    }

    public BookingDAO(Long id, String orderNumber, List<SeatDAO> items, double orderPrice, String stockStatus, String stockStatusReason, String paymentStatus, String paymentStatusReason) {
        this.id = id;
        this.bookingNumber = orderNumber;
        this.seats = items;
        this.bookingPrice = orderPrice;
        this.seatStatus = stockStatus;
        this.seatStatusReason = stockStatusReason;
        this.paymentStatus = paymentStatus;
        this.paymentStatusReason = paymentStatusReason;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getBookingNumber() {
        return bookingNumber;
    }

    public void setBookingNumber(String orderNumber) {
        this.bookingNumber = orderNumber;
    }

    public List<SeatDAO> getSeats() {
        return seats;
    }

    public void setSeats(List<SeatDAO> items) {
        this.seats = items;
    }

    public double getBookingPrice() {
        return bookingPrice;
    }

    public void setBookingPrice(double orderPrice) {
        this.bookingPrice = orderPrice;
    }

    public String getSeatStatus() {
        return seatStatus;
    }

    public String getSeatStatusReason() {
        return seatStatusReason;
    }

    public void setSeatStatus(String status) {
        this.seatStatus = status;
    }

    public void setSeatStatusReason(String statusReason) {
        this.seatStatusReason = statusReason;
    }

    public String getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(String paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    public String getPaymentStatusReason() {
        return paymentStatusReason;
    }

    public void setPaymentStatusReason(String paymentReason) {
        this.paymentStatusReason = paymentReason;
    }
}
