package com.company.event_calendar.user.dto;

import com.company.event_calendar.user.entity.UserRole;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class UserResponseDto {
    private String username;
    private String profileUrl;
    private UserRole role;
}
