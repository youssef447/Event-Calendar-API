package com.company.event_calendar.auth;

import com.company.event_calendar.config.response.ApiResponseBody;
import com.company.event_calendar.user.dto.UserLoginDto;
import com.company.event_calendar.user.dto.UserRegistrationDto;
import com.company.event_calendar.user.dto.UserResponseDto;
import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthenticationController {

    private final AuthenticationService service;

    @PostMapping("/register")
    public ApiResponseBody register(
            @Valid @ModelAttribute UserRegistrationDto request,
            @RequestParam(value = "image", required = false) MultipartFile imageFile) {

        UserResponseDto result = service.register(request, imageFile);

        return new ApiResponseBody("User registered successfully", result, true);

    }

    @PostMapping("/login")
    public ApiResponseBody login(@Valid @RequestBody UserLoginDto request) {
        UserResponseDto result = service.login(request);

        return new ApiResponseBody("Login successful", result, true);
    }

}
