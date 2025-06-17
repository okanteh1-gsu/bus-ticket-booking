

package com.omarkanteh.busbooking.mappers;

import com.omarkanteh.busbooking.dto.CreateScheduleRequest;
import com.omarkanteh.busbooking.dto.ScheduleDto;
import com.omarkanteh.busbooking.dto.UpdateScheduleRequest;
import com.omarkanteh.busbooking.entities.BusSchedule;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface BusScheduleMapper {

    @Mapping(target = "busId", source = "bus.id")
    @Mapping(target = "routeId", source = "route.id")
    ScheduleDto toDto(BusSchedule schedule);

    BusSchedule toEntity(CreateScheduleRequest request);

    void updateFromRequest(UpdateScheduleRequest request, @MappingTarget BusSchedule schedule);

}
