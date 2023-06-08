package com.tga.booking.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

import com.tga.common.entity.Booking;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class BookingPojoWrapper {
    private List<Booking> booking;
    
}
