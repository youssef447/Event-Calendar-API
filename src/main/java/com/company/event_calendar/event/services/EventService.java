package com.company.event_calendar.event.services;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.web.multipart.MultipartFile;
import org.springframework.stereotype.Service;

import com.company.event_calendar.config.exceptions.classes.NoEventFoundException;
import com.company.event_calendar.event.models.Event;
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
    public Event createEvent(Event event, MultipartFile imageFile) {
        UserEntity currentUser = currentUserService.getCurrentUser();
        event.setUser(currentUser);
        final Event result = eventRepository.save(event);
        if (imageFile != null && !imageFile.isEmpty()) {
            String fileName = fileStorageService.store(imageFile);
            event.setImageFileUrl(fileName);
        }
        return result;
    }

    public List<Event> getAllEvents() {
        UserEntity currentUser = currentUserService.getCurrentUser();
        return eventRepository.findByUser(currentUser);
    }

    public Event getEventById(Long id) {
        return eventRepository.findById(id)
                .orElseThrow(() -> new NoEventFoundException());
    }

    public List<Event> getEventsByDateRange(LocalDateTime start, LocalDateTime end) {
        UserEntity currentUser = currentUserService.getCurrentUser();

        return eventRepository.findEventsByUserAndDateRange(currentUser, start, end)
                .orElseThrow(() -> new NoEventFoundException());
    }

    @Transactional
    public Event updateEvent(Long id, Event eventDetails, MultipartFile imageFile) {
        Event event = getEventById(id);
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
    public void deleteEvent(Long id) {
        Event event = getEventById(id);

        eventRepository.delete(event);
        // Delete image if exists
        if (event.getImageFileUrl() != null) {
            fileStorageService.delete(event.getImageFileUrl());
        }
    }

}
