package com.tga.booking.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.*;

import javax.persistence.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class SeatDAO {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String seatNumber;
    @ManyToOne
    @JoinColumn(name = "order_id")
    @JsonBackReference
    private BookingDAO order;
    private long noOfSeat;
}
