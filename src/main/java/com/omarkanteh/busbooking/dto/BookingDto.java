package com.omarkanteh.busbooking.dto;

import com.omarkanteh.busbooking.enums.BookingStatus;

import jakarta.validation.constraints.*;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Builder
@Data
public class BookingDto {
    private long id;

    @Positive(message = "User ID must be positive")
    private long userId;

    @Positive(message = "Schedule ID must be positive")
    private long scheduleId;

    @NotNull(message = "Booking time cannot be null")
    private LocalDateTime bookingTime;

    @NotNull(message = "Booking status cannot be null")
    private BookingStatus bookingStatus;

    @NotNull(message = "Total fare cannot be null")
    @DecimalMin(value = "0.0", inclusive = false, message = "Total fare must be greater than 0")
    private BigDecimal totalFare;

}
