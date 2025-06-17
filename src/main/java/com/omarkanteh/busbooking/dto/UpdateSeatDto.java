package com.omarkanteh.busbooking.dto;

import lombok.Data;

@Data
public class UpdateSeatDto {
    private String seatNumber;
    private boolean isBooked;
    private Long bookingId;
}
