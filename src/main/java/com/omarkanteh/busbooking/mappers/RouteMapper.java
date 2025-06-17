package com.omarkanteh.busbooking.mappers;

import com.omarkanteh.busbooking.dto.RouteDto;
import com.omarkanteh.busbooking.entities.Route;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface RouteMapper {
    RouteDto toDto(Route route);

    @Mapping(target = "id", ignore = true)
    Route toEntity(RouteDto routeDTO);

    @Mapping(target = "id", ignore = true)
    void updateEntityFromDto(RouteDto routeDTO, @MappingTarget Route route);
}
