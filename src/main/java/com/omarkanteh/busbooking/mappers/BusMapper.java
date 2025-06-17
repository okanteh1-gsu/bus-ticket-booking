package com.omarkanteh.busbooking.mappers;

import com.omarkanteh.busbooking.dto.BusDto;
import com.omarkanteh.busbooking.dto.CreateBusRequest;
import com.omarkanteh.busbooking.entities.Bus;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface BusMapper {
    @Mapping(target = "id", source = "id")
    BusDto toDto(Bus bus);
    @Mapping(target = "id", ignore = true)
    Bus toEntity(CreateBusRequest request);
    @Mapping(target = "id", ignore = true)
    void updateBusFromRequest(CreateBusRequest request, @MappingTarget Bus bus);
    
}
