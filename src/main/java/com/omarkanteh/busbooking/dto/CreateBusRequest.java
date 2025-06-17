package com.omarkanteh.busbooking.dto;

import com.omarkanteh.busbooking.enums.BusType;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;


@Data
public class CreateBusRequest {
    @NotBlank(message = "Bus number cannot be blank")
    private String busNumber;

    @NotBlank(message = "Bus name cannot be blank")
    private String busName;

    @Min(value = 1, message = "Total seats must be greater than 0")
    private int totalSeats;

    @NotNull(message = "Bus type is required")
    @Enumerated(EnumType.STRING)
    private BusType busType;

}
