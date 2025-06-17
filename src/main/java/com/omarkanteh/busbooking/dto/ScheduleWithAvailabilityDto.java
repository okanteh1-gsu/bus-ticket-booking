package com.omarkanteh.busbooking.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ScheduleWithAvailabilityDto {
    private ScheduleDto schedule;
    private int availableSeats;
}
