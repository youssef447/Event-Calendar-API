package com.company.event_calendar.config.exceptions.response;

import java.time.LocalDateTime;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Data
public class ApiErrorResponse {
    final LocalDateTime  time;
    final String message;
    final int statusCode;
}
