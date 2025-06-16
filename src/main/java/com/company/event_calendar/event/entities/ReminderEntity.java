package com.company.event_calendar.event.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

import com.company.event_calendar.config.notification.NotificationType;
import lombok.Setter;


@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ReminderEntity {
    @NotNull(message = "Reminder time is required")
    private LocalDateTime reminderTime;
    
    private NotificationType notificationType; // EMAIL, POPUP, etc.

    private boolean sent = false;
}
