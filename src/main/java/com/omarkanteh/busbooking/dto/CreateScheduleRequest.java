package com.omarkanteh.busbooking.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class CreateScheduleRequest {
        @NotNull(message = "Route ID is required.")
        private Long routeId;

        @NotNull(message = "Bus ID is required.")
        private Long busId;

        @NotNull(message = "Departure time is required.")
        @Future(message = "Departure time must be in the future.")
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
        private LocalDateTime departureTime;

        @NotNull(message = "Arrival time is required.")
        @Future(message = "Arrival time must be in the future.")
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
        private LocalDateTime arrivalTime;

        @NotNull
        @Positive(message = "Fare must be positive.")
        private BigDecimal fare;
}


