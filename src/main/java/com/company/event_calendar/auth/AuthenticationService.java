package com.company.event_calendar.auth;

import com.company.event_calendar.config.exceptions.classes.UserAlreadyExistsException;
import com.company.event_calendar.event.services.FileStorageService;
import com.company.event_calendar.user.entity.UserEntity;
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

  @Transactional
  public void register(UserEntity user, MultipartFile imageFile) {

    // if email already exists
    if (repository.existsByUsername(user.getUsername())) {
      throw new UserAlreadyExistsException();
    } 
    repository.save(user);
    if (imageFile != null && !imageFile.isEmpty()) {
      String url = fileStorageService.store(imageFile);
      user.setProfileUrl(url);
    }

  }

}
