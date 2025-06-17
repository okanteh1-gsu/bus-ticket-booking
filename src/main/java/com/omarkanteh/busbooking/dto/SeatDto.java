package com.omarkanteh.busbooking.dto;

import lombok.Data;

@Data
public class SeatDto {
    private Long id;

    private String seatNumber; // e.g., "SEAT 1", "SEAT 2"

    private boolean isBooked;

    private Long scheduleId;

    private Long bookingId;

}
