package com.company.event_calendar.auth;

import com.company.event_calendar.config.exceptions.classes.UserAlreadyExistsException;
import com.company.event_calendar.config.exceptions.response.ApiResponse;
import com.company.event_calendar.event.services.FileStorageService;
import com.company.event_calendar.user.dto.UserRegistrationDto;
import com.company.event_calendar.user.entity.UserEntity;
import com.company.event_calendar.user.mapper.UserMapper;
import com.company.event_calendar.user.repository.UserEntityRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
    private final UserEntityRepository repository;
    private final FileStorageService fileStorageService;
    private final UserMapper userMapper;

    @Transactional
    public ApiResponse register(UserRegistrationDto userRegistrationDto, MultipartFile imageFile) {

        // if email already exists
        if (repository.existsByUsername(userRegistrationDto.getUsername())) {
            throw new UserAlreadyExistsException();
        }
        UserEntity userEntity = userMapper.toEntity(userRegistrationDto);

        repository.save(userEntity);
        if (imageFile != null && !imageFile.isEmpty()) {
            String url = fileStorageService.store(imageFile);
            userEntity.setProfileUrl(url);
        }
        return new ApiResponse("User registered successfully", userMapper.toResponseDto(userEntity), "success");

    }

}
