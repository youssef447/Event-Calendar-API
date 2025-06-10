package com.company.event_calendar.config.security;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import com.company.event_calendar.event.repository.EventRepository;

import lombok.RequiredArgsConstructor;

@Component("eventSecurity")
@RequiredArgsConstructor

public class EventSecurity {

    private final EventRepository eventRepository;

    public boolean isOwner(Long eventId) {
        String currentUsername = SecurityContextHolder.getContext().getAuthentication().getName();
        return eventRepository.findById(eventId)
                .map(event -> event.getUser().getUsername().equals(currentUsername))
                .orElse(false);
    }
}