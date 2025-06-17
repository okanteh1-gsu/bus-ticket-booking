package com.omarkanteh.busbooking.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Seat {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String seatNumber; // e.g., "SEAT 1", "SEAT 2"

    private boolean isBooked;

    @ManyToOne
    @JoinColumn(name = "schedule_id")
    private BusSchedule schedule;

    @ManyToOne
    @JoinColumn(name = "booking_id")
    private Booking booking;


}

