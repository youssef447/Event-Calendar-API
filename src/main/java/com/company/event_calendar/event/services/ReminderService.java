package com.company.event_calendar.event.services;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.company.event_calendar.config.exceptions.classes.NoEventFoundException;
import com.company.event_calendar.config.exceptions.classes.NoReminderFoundException;
import com.company.event_calendar.config.notification.NotificationService;
import com.company.event_calendar.config.notification.NotificationType;
import com.company.event_calendar.event.entities.EventEntity;
import com.company.event_calendar.event.entities.ReminderEntity;
import com.company.event_calendar.event.repository.EventRepository;
import com.company.event_calendar.user.entity.UserEntity;
import com.company.event_calendar.user.service.CurrentUserService;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReminderService {

    private final EventRepository eventRepository;
    private final NotificationService notificationService;
    private final EmailService emailService;
    private final CurrentUserService currentUserService;


    /**
     * Add a reminder to an existing event
     */
    @Transactional
    public EventEntity addReminderToEvent(Long eventId, ReminderEntity reminder) {

        EventEntity event = eventRepository.findById(eventId)
                .orElseThrow(NoEventFoundException::new);

        // Set default values for the reminder

        // Validate reminder time is before event start time
        if (reminder.getReminderTime().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Reminder time cannot be in the past");
        } // Validate reminder time is before event start time
        if (reminder.getReminderTime().isAfter(event.getStartTime())) {
            throw new IllegalArgumentException("Reminder time must be before event start time");
        }
        // Validate reminder time is not already set
        if (event.getReminders().stream().anyMatch(r -> r.getReminderTime().equals(reminder.getReminderTime()))) {
            throw new IllegalArgumentException("Reminder time already exists for this event");
        }

        // Add a reminder to the event's collection
        event.getReminders().add(reminder);
        return event;

    }

    /**
     * Remove a reminder from an event by reminder time
     */
    @Transactional
    public void removeReminderFromEvent(Long eventId, LocalDateTime reminderTime) {
        EventEntity event = eventRepository.findById(eventId)
                .orElseThrow(NoEventFoundException::new);

        boolean removed = event.getReminders().removeIf(reminder -> reminder.getReminderTime().equals(reminderTime));

        if (!removed) {
            throw new NoReminderFoundException();
        }


    }


    /**
     * Add multiple quick reminders to an event (15 min, 1 hour, 1 day before)
     */
    @Transactional
    public EventEntity addQuickReminders(Long eventId) {
        EventEntity event = eventRepository.findById(eventId)
                .orElseThrow(NoEventFoundException::new);

        LocalDateTime eventStart = event.getStartTime();

        // Add 15 minutes before a reminder
        if (eventStart.minusMinutes(15).isAfter(LocalDateTime.now())) {
            ReminderEntity reminder15 = new ReminderEntity();
            reminder15.setReminderTime(eventStart.minusMinutes(15));
            reminder15.setNotificationType(NotificationType.POPUP);

            event.getReminders().add(reminder15);
        }

        // Add 1 hour before a reminder
        if (eventStart.minusHours(1).isAfter(LocalDateTime.now())) {
            ReminderEntity reminder1h = new ReminderEntity();
            reminder1h.setReminderTime(eventStart.minusHours(1));
            reminder1h.setNotificationType(NotificationType.POPUP);

            event.getReminders().add(reminder1h);
        }

        // Add 1 day before a reminder
        if (eventStart.minusDays(1).isAfter(LocalDateTime.now())) {
            ReminderEntity reminder1d = new ReminderEntity();
            reminder1d.setReminderTime(eventStart.minusDays(1));
            reminder1d.setNotificationType(NotificationType.EMAIL);

            event.getReminders().add(reminder1d);
        }

        return event;
    }

    /**
     * Background job that processes due reminders every 5 seconds
     */
    @Scheduled(fixedRate = 5000)
    @Transactional
    public void processReminders() {
        LocalDateTime now = LocalDateTime.now();
        List<EventEntity> events = eventRepository.findEventsWithPendingReminders(now);

        for (EventEntity event : events) {
            for (ReminderEntity reminder : event.getReminders()) {
                if (!reminder.isSent() && reminder.getReminderTime().isBefore(now)) {
                    sendReminderNotification(event, reminder);
                    reminder.setSent(true);
                }
            }

        }
    }

    /**
     * Send notification for a specific reminder to the current user
     */
    private void sendReminderNotification(EventEntity event, ReminderEntity reminder) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM dd, yyyy 'at' HH:mm");
        String eventTimeStr = event.getStartTime().format(formatter);

        String message = String.format("Reminder: %s starts %s",
                event.getTitle(),
                eventTimeStr);

        if (event.getAddress() != null) {
            message += " in " + event.getAddress();
        }

        log.info("Sending {} reminder for event: {}", reminder.getNotificationType(), event.getTitle());
        switch (reminder.getNotificationType()) {
            case EMAIL:
                emailService.sendEmail(event.getUser().getUsername(), event.getTitle(), message);

            case POPUP:
                notificationService.sendNotification(event.getUser().getUsername(), message,
                        reminder.getNotificationType());

            default:
                break;
        }

    }


    public long getPendingRemindersCount(Long eventId) {
        EventEntity event = eventRepository.findById(eventId)
                .orElseThrow(NoEventFoundException::new);

        return event.getReminders().stream()
                .filter(reminder -> !reminder.isSent())
                .count();
    }

    public long getTotalPendingRemindersCount() {
        UserEntity currentUser = currentUserService.getCurrentUser();

        List<EventEntity> events = eventRepository.findByUser(currentUser);
        long total = 0L;
        for (var event : events) {
            total += event.getReminders().stream()
                    .filter(reminder -> !reminder.isSent())
                    .count();

        }

        return total;
    }

    /**
     * Get all overdue reminders (reminders that should have been sent but weren't)
     */
    public List<EventEntity> getEventsWithOverdueReminders() {
        UserEntity currentUser = currentUserService.getCurrentUser();

        LocalDateTime now = LocalDateTime.now();
        return eventRepository.findByUserWithOverdueReminders(currentUser, now);
    }

}
