package com.omarkanteh.busbooking.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class UpdateScheduleRequest {

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    @Future(message = "Departure time must be in the future.")
    private LocalDateTime departureTime;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    @Future(message = "Arrival time must be in the future.")
    private LocalDateTime arrivalTime;

    private Long busId;

    private Long routeId;


    @Positive(message = "Fare must be positive.")
    private BigDecimal fare;
}
