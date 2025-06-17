package com.company.event_calendar.user.dto;

import com.company.event_calendar.user.entity.UserRole;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserResponseDto {
    private String username;
    private String profileUrl;
    private UserRole role;
}
