package com.company.event_calendar.config.exceptions.response;

import java.time.LocalDateTime;

import jakarta.annotation.PostConstruct;
import lombok.Data;
import lombok.Getter;


public class ApiResponse {
    private LocalDateTime time;
    private final String message;
    private Object data;
    private final String status;

    public ApiResponse(String message, Object data, String status) {
        this.time = LocalDateTime.now();
        this.message = message;
        this.data = data;
        this.status = status;
    }

    public ApiResponse(String message, String status) {
        this.time = LocalDateTime.now();
        this.message = message;

        this.status = status;
    }
}
