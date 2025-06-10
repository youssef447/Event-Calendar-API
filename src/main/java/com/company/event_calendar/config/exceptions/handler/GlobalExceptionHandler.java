package com.company.event_calendar.config.exceptions.handler;

import java.nio.file.AccessDeniedException;
import java.time.LocalDateTime;
import java.util.stream.Collectors;

import org.apache.http.HttpStatus;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.company.event_calendar.config.exceptions.classes.NoEventFoundException;
import com.company.event_calendar.config.exceptions.classes.NoReminderFoundException;
import com.company.event_calendar.config.exceptions.classes.UserAlreadyExistsException;
import com.company.event_calendar.config.exceptions.response.ApiErrorResponse;

import org.springframework.security.core.userdetails.UsernameNotFoundException;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BindException.class)
    public ResponseEntity<?> handleValidationErrors(BindException ex) {
        int statusCode = HttpStatus.SC_BAD_REQUEST;

        String errorMessages = ex.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.joining(", "));
        ApiErrorResponse response = new ApiErrorResponse(LocalDateTime.now(), errorMessages, statusCode);
        return ResponseEntity.status(statusCode).body(response);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<?> handleDataIntegrityErrors(DataIntegrityViolationException ex) {
        int statusCode = HttpStatus.SC_BAD_REQUEST;
        ApiErrorResponse response = new ApiErrorResponse(LocalDateTime.now(), ex.getMessage(), statusCode);
        return ResponseEntity.status(statusCode).body(response);
    }

    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<?> handleUsernameErrors(UsernameNotFoundException ex) {
        int statusCode = HttpStatus.SC_NOT_FOUND;
        ApiErrorResponse response = new ApiErrorResponse(LocalDateTime.now(), ex.getMessage(), statusCode);
        return ResponseEntity.status(statusCode).body(response);
    }

    @ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseEntity<?> handleUsernameErrors(UserAlreadyExistsException ex) {
        int statusCode = HttpStatus.SC_CONFLICT;
        ApiErrorResponse response = new ApiErrorResponse(LocalDateTime.now(), ex.getMessage(), statusCode);
        return ResponseEntity.status(statusCode).body(response);
    }

    @ExceptionHandler(NoEventFoundException.class)
    public ResponseEntity<?> handleNoEventErrors(NoEventFoundException ex) {
        int statusCode = HttpStatus.SC_NOT_FOUND;
        ApiErrorResponse response = new ApiErrorResponse(LocalDateTime.now(), ex.getMessage(), statusCode);
        return ResponseEntity.status(statusCode).body(response);
    }

    @ExceptionHandler(NoReminderFoundException.class)
    public ResponseEntity<?> handleNoReminderErrors(NoReminderFoundException ex) {
        int statusCode = HttpStatus.SC_NOT_FOUND;
        ApiErrorResponse response = new ApiErrorResponse(LocalDateTime.now(), ex.getMessage(), statusCode);
        return ResponseEntity.status(statusCode).body(response);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<?> handleIllegalArgumentErrors(IllegalArgumentException ex) {
        int statusCode = HttpStatus.SC_BAD_REQUEST;
        ApiErrorResponse response = new ApiErrorResponse(LocalDateTime.now(), ex.getMessage(), statusCode);
        return ResponseEntity.status(statusCode).body(response);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<String> handleAccessDenied(AccessDeniedException ex) {
        int statusCode = HttpStatus.SC_FORBIDDEN;

        return ResponseEntity.status(statusCode)
                .body("You're not allowed to access this resource.");
    }
}