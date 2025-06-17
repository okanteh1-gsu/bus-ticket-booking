package com.omarkanteh.busbooking.dto;

import jakarta.validation.constraints.Min;
import lombok.Data;

import java.util.List;

@Data
public class CreateBookingRequest {
    @Min(value = 1, message = "Schedule ID must be a positive number.")
    private long scheduleId;

    private List<Long> seatIds;

}
