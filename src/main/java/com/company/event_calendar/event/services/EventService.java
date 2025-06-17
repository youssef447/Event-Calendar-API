package com.company.event_calendar.event.services;

import java.time.LocalDateTime;
import java.util.List;

import com.company.event_calendar.config.response.ApiResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.stereotype.Service;

import com.company.event_calendar.config.exceptions.classes.NoEventFoundException;
import com.company.event_calendar.event.entities.EventEntity;
import com.company.event_calendar.event.repository.EventRepository;
import com.company.event_calendar.user.entity.UserEntity;
import com.company.event_calendar.user.service.CurrentUserService;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor

public class EventService {

    private final EventRepository eventRepository;
    private final CurrentUserService currentUserService;
    private final FileStorageService fileStorageService;

    @Transactional
    public EventEntity createEvent(EventEntity event, MultipartFile imageFile) {

        eventRepository.save(event);
        if (imageFile != null && !imageFile.isEmpty()) {
            String fileName = fileStorageService.store(imageFile);
            event.setImageFileUrl(fileName);
        }
        return event;
    }

    public List<EventEntity> getAllEvents() {
        UserEntity currentUser = currentUserService.getCurrentUser();
        return eventRepository.findByUser(currentUser);
    }

    public EventEntity getEventById(Long id) {
        return eventRepository.findById(id)
                .orElseThrow(NoEventFoundException::new);
    }

    public List<EventEntity> getEventsByDateRange(LocalDateTime start, LocalDateTime end) {
        UserEntity currentUser = currentUserService.getCurrentUser();

        return eventRepository.findByUserAndDateRange(currentUser, start, end)
                .orElseThrow(NoEventFoundException::new);
    }

    @Transactional
    public EventEntity updateEvent(Long id, EventEntity eventDetails, MultipartFile imageFile) {
        EventEntity event = getEventById(id);
        event.setTitle(eventDetails.getTitle());
        event.setDescription(eventDetails.getDescription());
        event.setStartTime(eventDetails.getStartTime());
        event.setEndTime(eventDetails.getEndTime());
        event.setAddress(eventDetails.getAddress());
        event.setColor(eventDetails.getColor());
        event.setAllDay(eventDetails.isAllDay());

        if (imageFile != null && !imageFile.isEmpty()) {
            // Delete old image if exists
            if (event.getImageFileUrl() != null) {
                fileStorageService.delete(event.getImageFileUrl());
            }

            String fileName = fileStorageService.store(imageFile);
            event.setImageFileUrl(fileName);
        }
        return event;


    }

    @Transactional
    public ApiResponseBody deleteEvent(Long id) {
        EventEntity event = getEventById(id);

        eventRepository.delete(event);
        // Delete image if exists
        if (event.getImageFileUrl() != null) {
            fileStorageService.delete(event.getImageFileUrl());
        }
        return new ApiResponseBody("event deleted successfully", getAllEvents(), true);

    }

}
