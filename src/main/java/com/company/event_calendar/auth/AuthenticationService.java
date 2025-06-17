package com.company.event_calendar.auth;

import com.company.event_calendar.config.exceptions.classes.UserAlreadyExistsException;
import com.company.event_calendar.event.services.FileStorageService;
import com.company.event_calendar.user.dto.UserLoginDto;
import com.company.event_calendar.user.dto.UserRegistrationDto;
import com.company.event_calendar.user.dto.UserResponseDto;
import com.company.event_calendar.user.entity.UserEntity;
import com.company.event_calendar.user.mapper.UserMapper;
import com.company.event_calendar.user.repository.UserEntityRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
    private final UserEntityRepository repository;
    private final FileStorageService fileStorageService;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;


    @Transactional
    public UserResponseDto register(UserRegistrationDto request, MultipartFile imageFile) {

        // if email already exists
        if (repository.existsByUsername(request.getUsername())) {
            throw new UserAlreadyExistsException();
        }

        request.setPassword(passwordEncoder.encode(request.getPassword()));

        UserEntity userEntity = userMapper.toEntity(request);

        repository.save(userEntity);
        if (imageFile != null && !imageFile.isEmpty()) {
            String url = fileStorageService.store(imageFile);
            userEntity.setProfileUrl(url);
        }
        return userMapper.toResponseDto(userEntity);

    }

    @Transactional
    public UserResponseDto login(UserLoginDto loginDto) {

        // Find user by username
        UserEntity userEntity = repository.findByUsername(loginDto.getUsername())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        // Verify password
        if (!passwordEncoder.matches(loginDto.getPassword(), userEntity.getPassword())) {

            throw new BadCredentialsException("Invalid credentials");
        }

        return userMapper.toResponseDto(userEntity);
    }

}
