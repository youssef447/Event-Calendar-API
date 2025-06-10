package com.company.event_calendar.config.notification;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

import java.util.HashMap;
import java.util.Map;

import org.springframework.messaging.simp.SimpMessagingTemplate;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final SimpMessagingTemplate messagingTemplate;

    public void sendNotification(String username, String message, NotificationType notificationType) {
        Map<String, String> payload = new HashMap<>();
        payload.put("content", message);

        messagingTemplate.convertAndSendToUser(username, "/queue/notifications", payload);
    }
}
