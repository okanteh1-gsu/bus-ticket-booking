package com.omarkanteh.busbooking.mappers;

import com.omarkanteh.busbooking.dto.BookingDto;
import com.omarkanteh.busbooking.dto.CreateBookingRequest;
import com.omarkanteh.busbooking.entities.Booking;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface BookingMapper {
    @Mapping(target = "userId", source = "user.id")
    @Mapping(target = "scheduleId", source = "schedule.id")
    @Mapping(target = "bookingStatus", source = "status")
    BookingDto toDto(Booking booking);
    Booking toEntity(CreateBookingRequest request);
}
