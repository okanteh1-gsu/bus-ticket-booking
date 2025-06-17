package com.omarkanteh.busbooking.mappers;


import com.omarkanteh.busbooking.dto.CreateSeatDto;
import com.omarkanteh.busbooking.dto.SeatDto;
import com.omarkanteh.busbooking.entities.Seat;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface SeatMapper {

    @Mapping(target = "bookingId", source = "booking.id")
    @Mapping(target = "scheduleId", source = "schedule.id")
    SeatDto toDto(Seat seat);


    Seat toEntity(CreateSeatDto seatDto);

    @Mapping(ignore = true, target = "id")
    void Update(SeatDto seatDto, @MappingTarget Seat seat);
}
