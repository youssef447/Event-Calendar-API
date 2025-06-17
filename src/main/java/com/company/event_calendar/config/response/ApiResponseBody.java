package com.company.event_calendar.config.response;

import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class ApiResponseBody {
    private final LocalDateTime time;
    private final String message;
    private Object data;
    private final  boolean success;

    public ApiResponseBody(String message, Object data, boolean success) {
        this.time = LocalDateTime.now();
        this.message = message;
        this.data = data;
        this.success = success;
    }

    public ApiResponseBody(String message, boolean success) {
        this.time = LocalDateTime.now();
        this.message = message;
        this.success = success;
    }
}
