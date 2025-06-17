package com.company.event_calendar.user.mapper;

import com.company.event_calendar.user.dto.UserLoginDto;
import com.company.event_calendar.user.dto.UserRegistrationDto;
import com.company.event_calendar.user.dto.UserResponseDto;
import com.company.event_calendar.user.entity.UserEntity;
import org.mapstruct.Mapper;

//The @Mapper annotation tells MapStruct to generate the implementation of this interface.
@Mapper(componentModel = "spring")
public interface UserMapper {
    UserEntity toEntity(UserRegistrationDto userRegistrationDTO);
    UserResponseDto toResponseDto(UserEntity entity);

}

