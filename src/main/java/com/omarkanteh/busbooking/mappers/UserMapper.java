package com.omarkanteh.busbooking.mappers;

import com.omarkanteh.busbooking.dto.RegisterUserRequest;
import com.omarkanteh.busbooking.dto.UserDto;
import com.omarkanteh.busbooking.entities.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;


@Mapper(componentModel = "spring")
public interface UserMapper {
    UserDto toDto(User user);

    User toEntity(RegisterUserRequest request);

    @Mapping(ignore = true, target = "id")
    void UpdateUserRequest(UserDto request, @MappingTarget User user);
}
