package com.omarkanteh.busbooking.dto;

import lombok.Data;

@Data
public class CreateSeatDto {
    private Long id;
    private String seatNumber;
    private Long scheduleId;
    private boolean isBooked;
}
