package com.company.event_calendar.event.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

import com.company.event_calendar.config.notification.NotificationType;


@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Reminder {
    @NotNull(message = "Reminder time is required")
    private LocalDateTime reminderTime;
    
    private NotificationType notificationType; // EMAIL, POPUP, etc.

    private boolean sent = false;}
